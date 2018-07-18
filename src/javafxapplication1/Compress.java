/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

    /// <summa
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


    /// Compression Class
    /// </summary>
    class Compress
    {
        // Size of the binary buffer
        private static final int BUFFER_SIZE = 1024 * 1024; // 1 MB

        // Buffers
        private static byte[] binaryBuffer;
        private static char[] stringBuffer;

        // Dictionary of DNA letters to 2 bit values and 2 bit values to DNA letters
        public static HashMap<Character, Integer> LettersToDigit;
        public static HashMap<Integer, Character> DigitToLetter;




            static{
            // Initialize buffers
            binaryBuffer = new byte[BUFFER_SIZE];
            stringBuffer = new char[4 * BUFFER_SIZE];


            // Creates dictionary of DNA letters to 2 bits values
            LettersToDigit = new HashMap();
            LettersToDigit.put('A',  0);
            LettersToDigit.put('C', 1);
            LettersToDigit.put('T', 2);
            LettersToDigit.put('G', 3);



            // Creates dictionary of 2 bits values to DNA letters
            DigitToLetter = new HashMap();
            DigitToLetter.put(0, 'A');
            DigitToLetter.put(1, 'C');
            DigitToLetter.put(2, 'T');
            DigitToLetter.put(3, 'G');
            }





        /// <summary>
        /// Puts the input compressed in binaryBuffer (without a header)
        /// 
        /// --------------Compression Algorithm-------------------
        /// 1) Divide the input to blocks of 4 bytes
        /// 2) Compress every block to a single byte by substituding each character with its 2 bit value
        /// </summary>
        /// 
        /// <param name="input">A char[] containing the letters: A,C,T and G</param>
        /// <param name="size">Length of data in the "input" parameter</param>
        /// <returns>Index of the first unwritten cell in the binaryBuffer</returns>
        private static int CompressInput(char[] input, int size) throws Exception
        {
            // Check if the input is too big for the binaryBuffer
            if (input.length > 4 * BUFFER_SIZE)
            {
                // Throw Expception if the input buffer is too big
                throw new Exception(String.format("Input too big for buffer. \nMake sure the input is at most 4*BUFFER_SIZE ({1}) characters long", BUFFER_SIZE, 4 * BUFFER_SIZE));
            }
            byte compressedValue; // value of compressed block
            byte length; // Size of block

            // Index of the first unwritten cell in the binaryBuffer
            // Initialized to 0 because the binaryBuffer is empty
            int index = 0;


            // Iterate over blocks of 4 bytes in the input array
            for (int i = 0; i < size; i += 4)
            {
                // Compressed value of the current block
                // Initialized to 0 because no characters were read
                compressedValue = 0;

                // Find the size of the next block
                // 4 by default. Shorter if reached the end of the input.
                length = (byte)Math.min(4, size - i);



                // Iterate over the next block
                for (int j = 0; j < length; j++)
                {
                    // Shift value 2 bits left to make room for the next character in the input
                    compressedValue *= 4;

                    // Return an error if the input contain invalid characters
                    if (!LettersToDigit.containsKey(input[i + j]))
                        return -1;

                    // Add the 2 bit value of the current character
                    compressedValue += LettersToDigit.get(input[i + j]);
                }



                // Shift left the value when the byte is not full
                compressedValue *= (byte)Math.pow(4, 4 - length);

                // adds byte to binaryBuffer
                binaryBuffer[index] = compressedValue;

                // Increment last unwritten index in the binaryBuffer.
                index++;
            }

            return index;
        }



        /// <summary>
        /// Compress file
        /// 
        /// ---------------------Format---------------------
        /// byte :    0                   1                ...
        /// value: |HEADER|letter1,letter2,letter3,letter4|...
        /// HEADER = number of letters in last byte
        /// </summary>
        /// <param name="inputFilePath">Path to the input file</param>
        /// <param name="outputFilePath">Path to the output file</param>
        /// <returns>Success code: 0 = Success; -1 = Invalid input; -2 = IOException;</returns>
        public static int CompressFile(String inputFilePath, String outputFilePath)
        {
            // Header of the compressed file
            // Contains the number of letters in the last byte
            byte header = 0;


            int size; // Number of bytes read from the input
            int index; // Number of bytes in the output buffer
            //char[] stringBuffer = new char[4 * BUFFER_SIZE]; // Input buffer

            RandomAccessFile binaryWriter = null; // Output binary stream
            FileReader streamReader = null; // Input stream

            try
            {
                // Open input and output streams
                binaryWriter = new RandomAccessFile(outputFilePath, "rw");
                streamReader = new FileReader(inputFilePath);


                // Read BUFFER_SIZE bytes from the input file into stringBuffer
                size = streamReader.read(stringBuffer, 0, stringBuffer.length);

                // Write placeholder value for the header in the first byte of the output
                binaryWriter.write((byte)0);

                // Compress while the input is not empty
                while (size > 0)
                {
                    // Compresses stringBuffer
                    index = CompressInput(stringBuffer, size);

                    // Return error code if the compression failed
                    if (index < 0)
                        return -1;

                    // Write compressed buffer to the output file
                    binaryWriter.write(binaryBuffer, 0, index);

                    // Calculate header value
                    header = (byte)(size % 4);
                    if (header == 0)
                        header = 4;

                    // Read BUFFER_SIZE bytes from the input file into stringBuffer
                    size = streamReader.read(stringBuffer, 0, stringBuffer.length);
                }
                

                // Seek to start of the output file
                binaryWriter.seek(0);
                // Write header to output file
                binaryWriter.write(header);
            }
            catch (FileNotFoundException ex) {
                Logger.getLogger(Compress.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Compress.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Compress.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Catches IO Exceptions that were thrown during the compression process

            // Close input and output streams
            finally
            {
                try {
                    // Close binaryWriter stream
                    if (binaryWriter != null)
                        binaryWriter.close();
                    
                    // Close streamReader stream
                    if (streamReader != null)
                        streamReader.close();
                } catch (IOException ex) {
                    Logger.getLogger(Compress.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            // Success code
            return 0;
        }







        /// <summary>
        /// This function decompresses the input parameter
        /// </summary>
        /// <param name="input">A buffer contaning compressed DNA data</param>
        /// <param name="size">number of bytes in the input buffer</param>
        private static void DecompressInput(byte[] input, int size)
        {
            char letter;
            int val;

            // iterate over the input
            for (int i = 0; i < size; i++)
            {
                
                val = (input[i] & 0xFF);
                
                // decompress a single byte (4 letters)
                for (int j = 0; j < 4; j++)
                {
                    // convert the 2 LSB in the decompressed cell to the letter they represent
                    letter = DigitToLetter.get(val % 4);
                    
                    // Shift the data in the cell two bits to the left (get rid of already decompressed data)
                    val /= 4;

                    // Add the new letter to the ouput buffer
                    stringBuffer[4 * i + (3 - j)] = letter;
                }
            }
        }



        /// <summary>
        /// Decompress file (.ditx)
        /// </summary>
        /// <param name="inputFilePath">Path to the input file (compressed .ditx)</param>
        /// <param name="outputFilePath">Path to the output file (fasta .fa)</param>
        /// <returns></returns>
        public static int DecompressFile(String inputFilePath, String outputFilePath) throws IOException
        {
            // Header of the compressed file
            // Contains the number of letters in the last byte
            byte header;

            // Number of bytes read from the input
            int size; 

            // Number of bytes to write to the output
            int writeSize;

            DataInputStream binaryReader = null; // Input Binary stream
            PrintWriter streamWriter = null; // Output stream

            try
            {
                // Open stream to the input file
                FileInputStream fs = new FileInputStream(inputFilePath);
                
                // If the file stream is null (can't open the input file) return -2 (IO error).
                if (fs == null)
                    return -2;

                // Find the file size
                long fileLength = fs.getChannel().size();

                // Open input and output streams
                streamWriter = new PrintWriter(new FileOutputStream(outputFilePath), true);
                binaryReader = new DataInputStream(fs);
                
                if (binaryReader.available() <= 0)
                    return -1;
                
                
                // Read the header from the compressed file
                header = binaryReader.readByte();

                // Read BUFFER_SIZE bytes from the input file into stringBuffer
                size = binaryReader.read(binaryBuffer, 0, binaryBuffer.length);

                // decompress while the input is not empty
                while (size > 0)
                {
                    // decompress binaryBuffer
                    DecompressInput(binaryBuffer, size);

                    // Set number of characters to write to the ouput file
                    writeSize = 4*size;
                    if (fs.getChannel().position() == fileLength)
                        writeSize -= 4 - header;

                    // Write decompressed buffer to the output file
                    streamWriter.write(stringBuffer, 0, writeSize);

                    // Read BUFFER_SIZE bytes from the input file into binaryBuffer
                    size = binaryReader.read(binaryBuffer, 0, binaryBuffer.length);
                }
            }
            // Close input and output streams
            finally
            {
                // Close binaryReader stream
                if (binaryReader != null)
                    binaryReader.close();

                // Close streamReader stream
                if (streamWriter != null)
                    streamWriter.close();
            }

            // Success code
            return 0;
        }
    }