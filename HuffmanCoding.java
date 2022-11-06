package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;


/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Sara Phondge
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        /* Your code goes here */
            StdIn.setFile(fileName);
            sortedCharFreqList = new ArrayList<CharFreq>();
            String[] charList = new String[128];
            int size=0;
            while(StdIn.hasNextChar()) {
                char ch = StdIn.readChar();      // aaaabbbccd
                size++;
                if (charList[ch] != null) {
                     int freq = Integer.parseInt(charList[ch]);
                     freq++;
                     charList[ch]=Integer.toString(freq);
                 } else { 
                     charList[ch]= "1";
                 }
            }
            for (int i=0; i<charList.length; i++){
                if( charList[i] !=null ) 
                    sortedCharFreqList.add(new CharFreq(Character.valueOf((char)i),getProb(size, Double.parseDouble(charList[i]))));
            }
            checkForSingleChar();
    
            Collections.sort(sortedCharFreqList);
        }
    
        // Calculate and return probablity of occurance of a character
        private double getProb(int size, double count){
            return count/size;
        }
    
        // Check if there is only one character in the file and add a next  char in ASCII list with probability 0
        private void checkForSingleChar(){
            if (sortedCharFreqList.size() == 1 && sortedCharFreqList.get(0).getCharacter() != null) {
                char ch = sortedCharFreqList.get(0).getCharacter().charValue();
                int DECIMAL = 0;
                if ((int)ch != 127) 
                    DECIMAL = (int)ch+1;
                
                char ch2 = (char)DECIMAL;
                sortedCharFreqList.add(new CharFreq(Character.valueOf(ch2),0));
            }
        }
	/* Your code goes here */

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {

        /* Your code goes here */
        Queue<TreeNode> source = createSource(new Queue<TreeNode>());
        Queue<TreeNode> target = new Queue<TreeNode>();
        //int size = source.size();
        //System.out.println("size "+ size);
        while(!source.isEmpty()) {
                if (target.isEmpty() ) {
                    TreeNode firstNode = source.peek();
                    source.dequeue();
                    TreeNode secondNode = source.peek();
                    source.dequeue();
                    target.enqueue(createNewNode(firstNode,secondNode));
                } else {
                    TreeNode firstNode1 = compareNodeProb(source.peek(), target.peek());
                    if (firstNode1.equals(source.peek())) {
                        source.dequeue();
                    } else {
                        target.dequeue();
                    }
                    if (!source.isEmpty() && !target.isEmpty()) {
                        TreeNode node1 = source.peek();
                        TreeNode node2 = target.peek();
                        TreeNode secondNode1 = compareNodeProb(node1, node2);
                        if (secondNode1.equals(source.peek())) {
                            source.dequeue();
                        } else {
                            target.dequeue();
                        }
                        target.enqueue(createNewNode(firstNode1,secondNode1));
                    } else {
                        TreeNode secondNode1;
                        if (!source.isEmpty()) {
                            secondNode1 = source.peek();
                            source.dequeue();
                        } else {
                            secondNode1 = target.peek();
                            target.dequeue();
                        }
                        target.enqueue(createNewNode(firstNode1, secondNode1));

                    }
                }

           }

           while (target.size()> 1) {
                TreeNode firstNode = target.peek();
                target.dequeue();
                TreeNode nextNode = target.peek();
                target.dequeue();
                target.enqueue(createNewNode(firstNode, nextNode));
            }

            if (source.isEmpty() && target.size() == 1) 
                    huffmanRoot = target.peek();
                
        }
        
    


    // Create and return source Queueu from a TreeNode sortedCharFreqList    
    private Queue<TreeNode> createSource (Queue<TreeNode> source){
        for(int j=0; j<sortedCharFreqList.size() ; j++) {
            TreeNode node = new TreeNode(sortedCharFreqList.get(j) , null, null);
            source.enqueue(node);
        }
        return source;
    }

    /**  
    *  Take two nodes, create new node with null charater and Prob = sum of first & second Prob, 
    *  and retrun the new node
    */
    private TreeNode createNewNode(TreeNode firstNode, TreeNode secondNode) {
        double totalPorbability = firstNode.getData().getProbOcc() + secondNode.getData().getProbOcc(); 
        CharFreq charFreq = new CharFreq(null, totalPorbability);
        TreeNode newNode = new TreeNode(charFreq, firstNode, secondNode );
        return newNode;
    }

    // Compare two nodes and return node with lowest prob. If Prob is same return the node from source Queue
    private TreeNode compareNodeProb(TreeNode sourceNode, TreeNode targetNode){
        TreeNode node;
        double sourceNodeProb = sourceNode.getData().getProbOcc();
        double targetNodeProb = targetNode.getData().getProbOcc();
        if (sourceNodeProb < targetNodeProb || sourceNodeProb == targetNodeProb) {
            node =  sourceNode;
        } else {
            node = targetNode;
        }
        return node;
    }


       

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
        public void makeEncodings() {

            /* Your code goes here */
        
                if (huffmanRoot == null) 
                    return; 
        
                TreeNode rootNode = huffmanRoot;
                encodings = new String[128];
                String code = "";
                generateCodes(rootNode, code, encodings, '.');
        
                
        
            }
        
            /**
             *  Recursive method for in order tree traversal
             * @param node Treenode to traverse on
             * @param s character code
             * @param encodingS String Array to hold the character code at the character position
             * @param c character 
             */
            private void generateCodes(TreeNode node, String s, String[] encodingS, char c) {
                if(node != null) {
                    if (node.getLeft() != null) {
                        if (node.getLeft().getData().getCharacter() != null)
                            c = node.getLeft().getData().getCharacter().charValue();
                        generateCodes (node.getLeft(), s + "0", encodingS, c);
                    }
        
                    if (node.getRight() != null) {
                        if (node.getRight().getData().getCharacter() != null)
                            c = node.getRight().getData().getCharacter().charValue();
                        generateCodes (node.getRight(), s + "1", encodingS, c);
                    }
                    
                    if (node.getLeft() == null && node.getRight() == null) 
                        encodingS[c] = s;
        
                }
        
            }

       
       


	/* Your code goes here */

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);

        for (int i = 0; i < encodings.length; i++){
            if (encodings[i] != null){
                System.out.println(encodings[i] + " " + i);
            }

        }
        /* Your code goes here */
        String bitString = "";
        while(StdIn.hasNextChar()) {
            char ch = StdIn.readChar();
            if ( (int)ch!=0)
                bitString = bitString + encodings[(int)ch]; //aaaabbbccd  0000101010111111110
        }


        

        writeBitString(encodedFile,bitString );

    }


	/* Your code goes here */
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);

    /* Your code goes here */
        String decodedString = readBitString(encodedFile);
        //System.out.println(decodedString);

        if (huffmanRoot == null) 
            return; 

        TreeNode node = huffmanRoot;
        String convString = "";

        while (decodedString.length()>0) { // 
            if(node != null) {
                char ch = decodedString.charAt(0);
                if (ch == '0') {
                    node = node.getLeft();
                } else {
                    node = node.getRight();
                }
                if(node != null) {
                    if (node.getData().getCharacter() != null) {
                        convString = convString + node.getData().getCharacter().toString(); // aaaabbbccd
                        decodedString = decodedString.substring(1); // 
                        node = huffmanRoot; 
                    } else {
                        decodedString = decodedString.substring(1); // 
                    }
              //  } else {
                   // decodedString = decodedString.substring(1);
               // }
            }       
        } 
    }

        while(convString.length()>0) { //
            char ch = convString.charAt(0);
            StdOut.print(ch);
            convString = convString.substring(1);
        }
    }

    


	/* Your code goes here */
    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
