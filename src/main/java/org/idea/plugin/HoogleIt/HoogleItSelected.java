package org.idea.plugin.HoogleIt;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.util.TextRange;

import java.awt.*;
import java.net.*;

/**
 * Created by #ROOT
 * Date: 20.02.14
 * Time: 20:35
 * Updated by Laura Dietz, Nov 24, 2018:  Read Hoogle URL from environment variable HOOGLE_URL
 * Contact me: alistar.neron@gmail.com
 * Contact me: mrkafk@gmail.com
 * Contact me: dietz@smart-cactus.org
 */
public class HoogleItSelected extends EditorAction {

    public HoogleItSelected(EditorActionHandler defaultHandler) {
        super(defaultHandler);
    }

    public HoogleItSelected() {
        this(new GHandler());
    }

    private static class GHandler extends EditorWriteActionHandler {
        private GHandler() {
        }

        @Override
        public void executeWriteAction(Editor editor, DataContext dataContext) {
            Document document = editor.getDocument();

            if (editor == null || document == null || !document.isWritable()) {
                return;
            }

            SelectionModel selectionModel = editor.getSelectionModel();

            // get the range of the selected characters
            TextRange charsRange = new TextRange(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd());

            int selWidth = Math.abs(charsRange.getEndOffset() - charsRange.getStartOffset());

            if(selWidth == 0) {
                selectionModel.selectWordAtCaret(true);
                charsRange = new TextRange(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd());
            }

            // get the string to search
            String searchText = document.getText().substring(charsRange.getStartOffset(), charsRange.getEndOffset());

            if( searchText.length() < 1 ) return;
            try {
                String headerUrlDefault = "https://www.haskell.org/hoogle/?hoogle=";
		String headerUrl = "";
		try {
			headerUrl= System.getenv().containsKey("HOOGLE_URL")?System.getenv("HOOGLE_URL"):headerUrlDefault;
		} catch (java.lang.SecurityException ex) {
			System.err.println("Security prevents access to environment variable \"HOOGLE_URL\". Using default \""+headerUrlDefault+"\". ");
			headerUrl = headerUrlDefault;
		} 
                String encodedUrl = URLEncoder.encode(searchText.replaceAll("\n", ""),"UTF-8");

                GHandler.openURI(new URL(headerUrl+encodedUrl).toURI());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public static void openURI(URI uri) {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
