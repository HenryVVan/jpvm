package org.jpvm.pycParser;

import java.io.FileInputStream;
import java.io.IOException;


/**
 *     .pyc file structure
 *     +----------------+
 *     |  Magic Number  | 4 bytes
 *     |   Bite Field   | 4 bytes
 *     |  Modified Date | 4 bytes
 *     |   File Size    | 4 bytes
 *     |  Coed Object   |
 *     +----------------+
 */
public class PycReader {

   private final String pyc;
   private int timestamp;
   private int magicNumber;
   private int bitFiled;
   private int mappingPyFileSize;
   private CodeObject codeObject;

   public PycReader(String filename) {
      pyc = filename;
   }

   public void doParse() throws IOException {
      var stream = new FileInputStream(pyc);
      magicNumber = BinaryUtil.nextInt(stream);
      bitFiled = BinaryUtil.nextInt(stream);
      timestamp = BinaryUtil.nextInt(stream);
      mappingPyFileSize = BinaryUtil.nextInt(stream);
      // release resources
      stream.close();
   }

   public int getMappingPyFileSize() {
      return mappingPyFileSize;
   }

   public int getTimestamp() {
      return timestamp;
   }

   public int getMagicNumber() {
      return magicNumber;
   }

   public int getBitFiled() {
      return bitFiled;
   }
}
