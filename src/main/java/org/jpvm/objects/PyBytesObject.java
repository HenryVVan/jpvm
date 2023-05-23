package org.jpvm.objects;

import org.jpvm.objects.pyinterface.PyArgs;

public class PyBytesObject extends PyObject implements PyArgs {

   private byte[] data;

   public byte[] getData() {
      return data;
   }

   public void setData(byte[] data) {
      this.data = data;
   }

   public PyBytesObject(byte[] data) {
      this.data = data;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("b'");
      for (int i = 0; i < data.length; ++i) {
         builder.append(Integer.toHexString((data[i] & 0xf0) >> 4));
         builder.append(Integer.toHexString(data[i] & 0xf));
      }
      builder.append("'");
      return builder.toString();
   }

   @Override
   public Object toJavaType() {
      return data;
   }
}
