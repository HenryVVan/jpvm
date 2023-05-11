package org.jpvm.pycParser;

import org.jpvm.objects.*;


import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Marshal {

   /**
    * Masks for co_flags of CodeObject
    */
   public static int CO_OPTIMIZED = 0x0001;
   public static int CO_NEWLOCALS = 0x0002;
   public static int CO_VARARGS = 0x0004;
   public static int CO_VARKEYWORDS = 0x0008;
   public static int CO_NESTED = 0x0010;
   public static int CO_GENERATOR = 0x0020;

   public static class TYPE {
      public static final byte FLAG_REF = (byte) 0x80;
      public static final byte TYPE_NULL = '0';
      public static final byte TYPE_NONE = 'N';
      public static final byte TYPE_FALSE = 'F';
      public static final byte TYPE_TRUE = 'T';
      public static final byte TYPE_STOPITER = 'S';
      public static final byte TYPE_ELLIPSIS = '.';
      public static final byte TYPE_INT = 'i';
      /* TYPE_INT64 is not generated anymore.
      Supported for backward compatibility only. */
      public static final byte TYPE_INT64 = 'I';
      public static final byte TYPE_FLOAT = 'f';
      public static final byte TYPE_BINARY_FLOAT = 'g';
      public static final byte TYPE_COMPLEX = 'x';
      public static final byte TYPE_BINARY_COMPLEX = 'y';
      public static final byte TYPE_LONG = 'l';
      public static final byte TYPE_STRING = 's';
      public static final byte TYPE_INTERNED = 't';
      public static final byte TYPE_REF = 'r';
      public static final byte TYPE_TUPLE = '(';
      public static final byte TYPE_LIST = '[';
      public static final byte TYPE_DICT = '{';
      public static final byte TYPE_CODE = 'c';
      public static final byte TYPE_UNICODE = 'u';
      public static final byte TYPE_UNKNOWN = '?';
      public static final byte TYPE_SET = '<';
      public static final byte TYPE_FROZENSET = '>';
      public static final byte TYPE_ASCII = 'a';
      public static final byte TYPE_ASCII_INTERNED = 'A';
      public static final byte TYPE_SMALL_TUPLE = ')';
      public static final byte TYPE_SHORT_ASCII = 'z';
      public static final byte TYPE_SHORT_ASCII_INTERNED = 'Z';
   }

   private PyListObject refs;

   /**
    * flag of being parsed {@link PyObject}
    */
   private int flag;

   public Marshal() {
       refs = new PyListObject();
   }


   public PyObject RREF(PyObject o) {
      if (flag != 0) {
         assert (flag & TYPE.FLAG_REF) != 0;
         if (o == null)
            return null;
         refs.app1(o);
      }
      return o;
   }

   public void RREFReserve() {
      if (flag != 0) {
         refs.app1(BuiltIn.None);
      }
   }

   public  PyObject loadPyObject(ByteBuffer buffer) {
      byte b = buffer.get();
      int code = b & 0xff;
      int type = code & (~TYPE.FLAG_REF);
      flag = code & TYPE.FLAG_REF;
      return switch (type) {
         case TYPE.TYPE_NULL -> BuiltIn.NULL;
         case TYPE.TYPE_NONE -> BuiltIn.None;
         case TYPE.TYPE_STOPITER -> BuiltIn.PyExcStopIteration;
         case TYPE.TYPE_ELLIPSIS -> BuiltIn.ELLIPSIS;
         case TYPE.TYPE_FALSE -> BuiltIn.False;
         case TYPE.TYPE_TRUE -> BuiltIn.True;
         case TYPE.TYPE_INT -> loadInt(buffer);
         case TYPE.TYPE_INT64 -> loadInt64(buffer);
         case TYPE.TYPE_SMALL_TUPLE -> loadSmallTuple(buffer);
         case TYPE.TYPE_TUPLE -> loadTuple(buffer);
         case TYPE.TYPE_CODE -> loadCodeObject(buffer);
         case TYPE.TYPE_LIST -> loadList(buffer);
         case TYPE.TYPE_LONG -> loadLong(buffer);
         case TYPE.TYPE_FLOAT -> loadFloat(buffer);
         case TYPE.TYPE_BINARY_FLOAT -> loadBinaryFloat(buffer);
         case TYPE.TYPE_COMPLEX -> throw  new RuntimeException("Unsupported TYPE_COMPLEX");
         case TYPE.TYPE_BINARY_COMPLEX -> loadComplex(buffer);
         case TYPE.TYPE_STRING -> loadPyStringObject(buffer); // load bytes
         case TYPE.TYPE_ASCII_INTERNED, TYPE.TYPE_INTERNED -> loadACSII(buffer, true, false);
         case TYPE.TYPE_ASCII, TYPE.TYPE_UNICODE -> loadACSII(buffer, false, false);
         case TYPE.TYPE_SHORT_ASCII -> loadACSII(buffer, false, true);
         case TYPE.TYPE_SHORT_ASCII_INTERNED -> loadACSII(buffer, true, true);
         case TYPE.TYPE_DICT -> loadDictionary(buffer);
         case TYPE.TYPE_SET -> loadSet(buffer, true);
         case TYPE.TYPE_FROZENSET -> loadSet(buffer, false);
         case TYPE.TYPE_REF -> loadReference(buffer);
         default -> throw new IllegalStateException("Unexpected value: " + type);
      };
   }

   public PyUnicodeObject loadACSII(ByteBuffer buffer, boolean interned, boolean isShort) {
      int size;
      if(isShort)
         size = buffer.get();
      else
         size = buffer.getInt();
      byte[] bytes = new byte[size];
      buffer.get(bytes);
      PyUnicodeObject o = new PyUnicodeObject(bytes);
      o = (PyUnicodeObject)RREF(o);
      return o;
   }

   /**
    * see cpython(3.8.16)/Python/marshal.c:953 r_object(RFILE *p)
    * @param stream byte[]
    * @return {@link CodeObject}
    */
   public  CodeObject loadCodeObject(byte[] stream) {
      ByteBuffer buffer = ByteBuffer.wrap(stream).order(ByteOrder.LITTLE_ENDIAN);
      return (CodeObject)loadPyObject(buffer);
   }

   public  CodeObject loadCodeObject(FileInputStream stream) throws IOException {
      var size = stream.available();
      byte[] bytes = new byte[size];
      var s = stream.read(bytes);
      ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
      return (CodeObject)loadPyObject(buffer);
   }

   public  CodeObject loadCodeObject(ByteBuffer buffer) {
      CodeObject codeObject = new CodeObject();
      codeObject.setCoArgument(buffer.getInt());
      codeObject.setCoPosOnlyArCnt(buffer.getInt());
      codeObject.setCoKwOnlyArCnt(buffer.getInt());
      codeObject.setCoNLocals(buffer.getInt());
      codeObject.setCoStackSize(buffer.getInt());
      codeObject.setCoFlags(buffer.getInt());

      codeObject.setCoCode(loadPyObject(buffer));
      codeObject.setCoConsts(loadPyObject(buffer));
      codeObject.setCoNames(loadPyObject(buffer));
      codeObject.setCoVarNames(loadPyObject(buffer));
      codeObject.setCoFreeVars(loadPyObject(buffer));
      codeObject.setCoCellVars(loadPyObject(buffer));
      codeObject.setCoFileName(loadPyObject(buffer));
      codeObject.setCoName(loadPyObject(buffer));
      codeObject.setCoFirstLineNo(buffer.getInt());
      codeObject.setColnotab(loadPyObject(buffer));
      return codeObject;
   }

   public PyFloatObject loadBinaryFloat(ByteBuffer buffer) {
      PyFloatObject floatObject = new PyFloatObject(buffer.getDouble());
      RREF(floatObject);
      return floatObject;
   }


   private PyObject loadReference(ByteBuffer buffer) {
      int i = buffer.getInt();
      return refs.get(i - 1);
   }

   private  PySetObject loadSet(ByteBuffer buffer, boolean isFrozen) {
      int size = buffer.getInt();
      if (isFrozen && size == 0) {
         RREF(BuiltIn.FROZENSET);
         return BuiltIn.FROZENSET;
      }else {
         PySetObject setObject = new PySetObject(isFrozen);
         RREF(setObject);
         for (int i = 0; i < size; i++) {
            setObject.put(loadPyObject(buffer));
         }
         return setObject;
      }
   }

   private  PyObject loadDictionary(ByteBuffer buffer) {
      PyDictObject dictObject = new PyDictObject();
      for(;;) {
         PyObject key = loadPyObject(buffer);
         if (key == BuiltIn.NULL) break;
         PyObject val = loadPyObject(buffer);
         if (val == BuiltIn.NULL) break;
         dictObject.put(key, val);
      }
      RREF(dictObject);
      return dictObject;
   }

   private  PyListObject loadList(ByteBuffer buffer) {
      int size = buffer.getInt();
      PyListObject listObject = new PyListObject(size);
      for (int i = 0; i < size; i++) {
         listObject.app1(loadPyObject(buffer));
      }
      RREF(listObject);
      return listObject;
   }

   private  PyComplexObject loadComplex(ByteBuffer buffer) {
      PyComplexObject object = new PyComplexObject(loadBinaryFloat(buffer), loadBinaryFloat(buffer));
      RREF(object);
      return object;
   }

   private  PyFloatObject loadFloat(ByteBuffer buffer) {
      int size = buffer.get();
      if (size == 4) {
         PyFloatObject floatObject = new PyFloatObject(buffer.getFloat());
         RREF(floatObject);
         return floatObject;
      }
      else if (size == 8) {
         PyFloatObject floatObject = new PyFloatObject(buffer.getDouble());
         RREF(floatObject);
         return floatObject;
      }
      throw new RuntimeException("jpython can not convert " + size + "bytes into a PyFloat");
   }

   private  PyObject loadLong(ByteBuffer buffer) {
      throw new RuntimeException("jpython do not support big int like cpython");
   }

   private  PyLongObject loadInt64(ByteBuffer buffer) {
      PyLongObject longObject = new PyLongObject(buffer.getLong());
      RREF(longObject);
      return longObject;
   }

   private  PyLongObject loadInt(ByteBuffer buffer) {
      PyLongObject longObject = new PyLongObject(buffer.getInt());
      RREF(longObject);
      return longObject;
   }

   private  PyTupleObject loadTuple(ByteBuffer buffer) {
      int s = buffer.getInt();
      PyTupleObject tupleObject = new PyTupleObject(s);
      for (int i = 0; i < s; ++i) {
         tupleObject.set(i, loadPyObject(buffer));
      }
      RREF(tupleObject);
      return tupleObject;
   }

   private  PyTupleObject loadSmallTuple(ByteBuffer buffer) {
      int s = buffer.get();
      PyTupleObject tupleObject = new PyTupleObject(s);
      for (int i = 0; i < s; ++i) {
         tupleObject.set(i, loadPyObject(buffer));
      }
      RREF(tupleObject);
      return tupleObject;
   }

   private  PyBytesObject loadPyStringObject(ByteBuffer buffer) {
      int size = buffer.getInt();
      byte[] bytes = new byte[size];
      buffer.get(bytes);
      PyBytesObject o = new PyBytesObject(bytes);
      RREF(o);
      return o;
   }
}