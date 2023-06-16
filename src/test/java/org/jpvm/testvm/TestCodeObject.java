package org.jpvm.testvm;

import org.jpvm.errors.PyException;
import org.jpvm.module.Disassembler;
import org.jpvm.module.Marshal;
import org.jpvm.pycParser.PyCodeObject;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

public class TestCodeObject {

  @Test
  public void testClosure() throws IOException, PyException {
    String filename = "/Users/huchang/IdeaProjects/jpvm/src/test/resources/closure/__pycache__/test01.cpython-38.pyc";
    FileInputStream stream = new FileInputStream(filename);
    stream.skipNBytes(16);
    int available = stream.available();
    byte[] bytes = new byte[available];
    int s = stream.read(bytes);
    Marshal marshal = new Marshal();
    PyCodeObject pyCodeObject = marshal.loadCodeObject(bytes);
    System.out.println(pyCodeObject);
    Disassembler disassembler = new Disassembler(pyCodeObject);
    disassembler.dis();
    stream.close();
  }
}
