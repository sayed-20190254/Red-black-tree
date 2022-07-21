/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package red.black.tree;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author dell
 */
public class main {
        public static void main(String[] args) throws IOException {
            RedBlackTree t1=new RedBlackTree();
            
            int s;
            
            while(true){
            Scanner in=new Scanner(System.in);
            s=in.nextInt();
            if(s==1){
            s=in.nextInt();
            t1.insertNode(s);
            }
            else{
                s=in.nextInt();
                t1.deleteNode(s);
                int data = t1.searchNode(s).data;
              
            }
            }
            
        }

    
}
