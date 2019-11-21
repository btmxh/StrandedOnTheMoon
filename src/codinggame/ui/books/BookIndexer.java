/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.books;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Welcome
 */
public class BookIndexer {
    public static void main(String[] args) throws IOException {
        String path = "/books/guidebook";
        int pageCount = new ImageBook(path).getPageCount();
        path = "D:\\NGO DUY ANH\\NetBeansProjects\\CodingGame\\res\\books\\guidebook\\";
        rename(path, "page (0).png", "page_0.png");
        int i =1;
        for (;i < Math.ceil(pageCount / 2f); i++) {
            rename(path, "page (" + (i * 2 - 1) + ").png", "page_" + i + "_0.png");
            rename(path, "page (" + (i * 2) + ").png", "page_" + i + "_1.png");
        }
        System.out.println(i * 2 - 1);
        if(i * 2 - 1 == pageCount)  return;
        rename(path, "page (" + (pageCount - 1) + ").png", "page_");
    }

    private static void rename(String path, String filename, String rename) throws IOException {
        if(1 + 1 == 2)  return;
        System.out.println(path + filename);
        System.out.println(path + rename);
        File renamedFile = new File(path + rename);
        try {
            renamedFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(BookIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        Files.move(new File(path + filename).toPath(), renamedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
