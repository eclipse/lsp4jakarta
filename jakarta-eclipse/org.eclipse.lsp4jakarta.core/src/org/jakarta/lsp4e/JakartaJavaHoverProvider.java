package org.jakarta.lsp4e;

import org.eclipse.jdt.ui.text.java.hover.IJavaEditorTextHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
//import org.eclipse.lsp4e.operations.hover.LSPTextHover;
import org.eclipse.ui.IEditorPart;

@SuppressWarnings("restriction")
// Temporarily commenting out as it is not being used
public class JakartaJavaHoverProvider implements IJavaEditorTextHover {
    
//    private LSPTextHover lspTextHover;

//    public JakartaJavaHoverProvider() {
//        super();
//        lspTextHover = new LSPTextHover();
//    }

    @Override
    public String getHoverInfo(ITextViewer textViewer, IRegion region) {
//        return this.lspTextHover.getHoverInfo(textViewer, region);
        return null;
    }

    @Override
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
//        return this.lspTextHover.getHoverRegion(textViewer, offset);
        return null;
    }

    @Override
    public void setEditor(IEditorPart editor) {

    }
}