package org.jakarta.codeAction.proposal;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.lsp4j.CodeActionKind;


/**
 * 
 * Code action proposal for deleting an existing annotation for MethodDeclaration/Field.
 *
 */
public class DeleteAnnotationProposal extends ChangeCorrectionProposal {
	 private final CompilationUnit fInvocationNode;
	 private final IBinding fBinding;

	 private final String[] annotations;
	 private final ASTNode declaringNode;
	 
	 
	 public DeleteAnnotationProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
	            IBinding binding, int relevance, ASTNode declaringNode, String... annotations) {
		 super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
	     this.fInvocationNode = invocationNode;
	     this.fBinding = binding;
	     this.declaringNode = declaringNode;
	     this.annotations = annotations;
	 }
	
	 @Override
	 protected ASTRewrite getRewrite() throws CoreException {
		 ASTNode declNode = this.declaringNode;
	     ASTNode boundNode = fInvocationNode.findDeclaringNode(fBinding);
	     CompilationUnit newRoot = fInvocationNode;
	     if (boundNode == null) {
	    	 newRoot = ASTResolving.createQuickFixAST(getCompilationUnit(), null);
	     }
	     ImportRewrite imports = createImportRewrite(newRoot);
	     
	     boolean isField = declNode instanceof VariableDeclarationFragment;
	     boolean isMethod = declNode instanceof MethodDeclaration;
	     
	     String[] annotations = getAnnotations();

	     // get short name of annotations
	     String[] annotationShortNames = new String[annotations.length];
	     for (int i = 0; i < annotations.length; i++) {
	    	 String shortName = annotations[i].substring(annotations[i].lastIndexOf(".") + 1, annotations[i].length());
	         annotationShortNames[i] = shortName;
	     }
	     
	     if (isField || isMethod) {
	    	 AST ast = declNode.getAST();
	    	 ASTRewrite rewrite = ASTRewrite.create(ast);
	    	 
	    	 ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(declNode, imports);
	    	 
	    	 // remove annotations in the removeAnnotations list
	    	 @SuppressWarnings("unchecked")
	    	 List<? extends ASTNode> children;
	    	 if(isMethod) {
	    		 children = (List<? extends ASTNode>) declNode
	    		    	 	.getStructuralProperty(MethodDeclaration.MODIFIERS2_PROPERTY);
	    	 } else {
	    		 declNode = declNode.getParent();
	    		 children = (List<? extends ASTNode>) declNode
	    		    	 	.getStructuralProperty(FieldDeclaration.MODIFIERS2_PROPERTY);
	    	 }
	    	 

	         // find and save existing annotation, then remove it from ast
	         for (ASTNode child : children) {
	        	 if (child instanceof Annotation) {
	        		 Annotation annotation = (Annotation) child;
	        		 // IAnnotationBinding annotationBinding = annotation.resolveAnnotationBinding();

	        		 boolean containsAnnotation = Arrays.stream(annotationShortNames)
	        				 .anyMatch(annotation.getTypeName().toString()::equals);
	        		 if (containsAnnotation) {
	        			 rewrite.remove(child, null);
	        		 }
	        	 }
	         }
	         
	         return rewrite;
	     }
	     
	     return null;
	 }
	 
	 /**
	  * Returns the Compilation Unit node
	  *
	  * @return the invocation node for the Compilation Unit
	  */
	  protected CompilationUnit getInvocationNode() {
		  return this.fInvocationNode;
	  }

	  /**
	   * Returns the Binding object associated with the new annotation change
	   * 
	   * @return the binding object
	   */
	   protected IBinding getBinding() {
		   return this.fBinding;
	   }

	  /**
	   * Returns the annotations list
	   * 
	   * @return the list of new annotations to add
	   */
	   protected String[] getAnnotations() {
		   return this.annotations;
	   }
}
