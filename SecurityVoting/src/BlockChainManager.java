import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;

// 1. The Block Class handles individual data points and hashing
class Block {
    public String hash;
    public String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    // Calculates the hash by combining all block data
    public String calculateHash() {
        String input = previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data;
        return applySha256(input);
    }

    // Proof of Work: Finding a hash that starts with 'difficulty' zeros
    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! Hash: " + hash);
    }

    // Helper method to generate SHA-256 digital signature
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

// 2. The BlockChainManager Class manages the list of blocks
public class BlockChainManager {
    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static int difficulty = 3;

    public static void main(String[] args) {
        System.out.println("--- Starting Blockchain ---");

        System.out.print("Mining block 1... ");
        addBlock("Genesis Block");

        System.out.print("Mining block 2... ");
        addBlock("Second Block Data");

        System.out.print("Mining block 3... ");
        addBlock("Third Block Data");

        // Validation Check
        System.out.println("\nIs the Blockchain valid? " + isChainValid());

        // Final Output
        System.out.println("\n--- The Chain Ledger ---");
        for (Block b : blockchain) {
            System.out.println("Hash: " + b.hash + " | Previous: " + b.previousHash);
        }
    }

    public static void addBlock(String data) {
        // Find the previous hash link
        String prevHash = blockchain.isEmpty() ? "0" : blockchain.get(blockchain.size() - 1).hash;

        // Pass parameters into the new Block
        Block newBlock = new Block(data, prevHash);

        // Mine the block with our difficulty setting
        newBlock.mineBlock(difficulty);

        // Add to the ledger
        blockchain.add(newBlock);
    }

    public static boolean isChainValid() {
        for (int i = 1; i < blockchain.size(); i++) {
            Block current = blockchain.get(i);
            Block previous = blockchain.get(i - 1);

            // Check if the current block's hash is valid
            if (!current.hash.equals(current.calculateHash())) {
                System.out.println("Integrity error: Current block hash mismatch.");
                return false;
            }
            // Check if the link to the previous block is correct
            if (!current.previousHash.equals(previous.hash)) {
                System.out.println("Link error: Previous hash reference mismatch.");
                return false;
            }
        }
        return true;
    }
}