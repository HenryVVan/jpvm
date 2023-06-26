package org.jpvm.objects;

import org.jpvm.errors.PyException;
import org.jpvm.errors.PyNotImplemented;
import org.jpvm.objects.types.PyPythonType;
import org.jpvm.objects.types.PyTypeType;
import org.jpvm.protocols.PyNumberMethods;
import org.jpvm.pvm.Abstract;

/** used in python code to represent a python object */
public class PyPythonObject extends PyObject implements PyNumberMethods {

  private PyTypeType type;

  public PyPythonObject() {
    dict = new PyDictObject();
  }

  @Override
  public String toString() {
    return super.toString();
  }

  @Override
  public PyObject getType() {
    return type;
  }

  public void setType(PyTypeType type) {
    this.type = type;
  }

  @Override
  public PyUnicodeObject getTypeName() {
    return type.getTypeName();
  }

  @Override
  public PyUnicodeObject str() {
    return super.str();
  }

  @Override
  public PyUnicodeObject repr() {
    return super.repr();
  }

  @Override
  public PyLongObject hash() {
    return super.hash();
  }

  @Override
  public PyBoolObject richCompare(PyObject o, Operator op) throws PyException {
    switch (op){
      case Py_LT -> {
        PyObject attr = getAttr(PyUnicodeObject.getOrCreateFromInternStringPool("__lt__", true));
        if(attr instanceof PyMethodObject func){
          PyTupleObject args = new PyTupleObject(1);
          args.set(0, o);
          return (PyBoolObject) Abstract.abstractCall(func, this, args, null);
        }
      }
      case Py_LE -> {
        PyObject attr = getAttr(PyUnicodeObject.getOrCreateFromInternStringPool("__le__", true));
        if(attr instanceof PyMethodObject func){
          PyTupleObject args = new PyTupleObject(1);
          args.set(0, o);
          return (PyBoolObject) Abstract.abstractCall(func, this, args, null);
        }
      }
      case Py_EQ -> {
        PyObject attr = getAttr(PyUnicodeObject.getOrCreateFromInternStringPool("__eq__", true));
        if(attr instanceof PyMethodObject func){
          PyTupleObject args = new PyTupleObject(1);
          args.set(0, o);
          return (PyBoolObject) Abstract.abstractCall(func, this, args, null);
        }
      }
      case Py_NE -> {
        PyObject attr = getAttr(PyUnicodeObject.getOrCreateFromInternStringPool("__ne__", true));
        if(attr instanceof PyMethodObject func){
          PyTupleObject args = new PyTupleObject(1);
          args.set(0, o);
          return (PyBoolObject) Abstract.abstractCall(func, this, args, null);
        }
      }
      case Py_GT -> {
        PyObject attr = getAttr(PyUnicodeObject.getOrCreateFromInternStringPool("__gt__", true));
        if(attr instanceof PyMethodObject func){
          PyTupleObject args = new PyTupleObject(1);
          args.set(0, o);
          return (PyBoolObject) Abstract.abstractCall(func, this, args, null);
        }
      }
      case Py_GE -> {
        PyObject attr = getAttr(PyUnicodeObject.getOrCreateFromInternStringPool("__ge__", true));
        if(attr instanceof PyMethodObject func){
          PyTupleObject args = new PyTupleObject(1);
          args.set(0, o);
          return (PyBoolObject) Abstract.abstractCall(func, this, args, null);
        }
      }
    }
    return super.richCompare(o, op);
  }

  @Override
  public PyDictObject getDict() {
    return super.getDict();
  }

  @Override
  public PyObject add(PyObject o) throws PyException {
    // get attribute from class
    PyObject attr = getAttr(PyUnicodeObject.getOrCreateFromInternStringPool("__add__", true));
    if (attr instanceof PyMethodObject func) {
      PyTupleObject args = new PyTupleObject(1);
      args.set(0, o);
      return Abstract.abstractCall(func, this, args, null);
    }
    return PyNumberMethods.super.add(o);
  }

  @Override
  public PyObject sub(PyObject o) throws PyException {
    PyObject attr = getAttr(PyUnicodeObject.getOrCreateFromInternStringPool("__sub__", true));
    if (attr instanceof PyMethodObject func) {
      PyTupleObject args = new PyTupleObject(1);
      args.set(0, o);
      return Abstract.abstractCall(func, this, args, null);
    }
    return PyNumberMethods.super.sub(o);
  }

  @Override
  public PyObject mul(PyObject o) throws PyException {
    PyObject attr = getAttr(PyUnicodeObject.getOrCreateFromInternStringPool("__mul__", true));
    if (attr instanceof PyMethodObject func) {
      PyTupleObject args = new PyTupleObject(1);
      args.set(0, o);
      return Abstract.abstractCall(func, this, args, null);
    }
    return PyNumberMethods.super.mul(o);
  }

  @Override
  public PyObject mod(PyObject o) throws PyException {
    PyObject attr = getAttr(PyUnicodeObject.getOrCreateFromInternStringPool("__mod__", true));
    if (attr instanceof PyMethodObject func) {
      PyTupleObject args = new PyTupleObject(1);
      args.set(0, o);
      return Abstract.abstractCall(func, this, args, null);
    }
    return PyNumberMethods.super.mod(o);
  }

  @Override
  public PyObject trueDiv(PyObject o) throws PyException {
    PyObject attr = getAttr(PyUnicodeObject.getOrCreateFromInternStringPool("__truediv__", true));
    if (attr instanceof PyMethodObject func) {
      PyTupleObject args = new PyTupleObject(1);
      args.set(0, o);
      return Abstract.abstractCall(func, this, args, null);
    }
    return PyNumberMethods.super.trueDiv(o);
  }

  @Override
  public PyObject abs() throws PyException {
    var t = (PyPythonType) type;
    PyObject res =
        t.callPythonCode(
            PyUnicodeObject.getOrCreateFromInternStringPool("__abs__", true),
            this,
            PyTupleObject.zero,
            null);
    if (res != null) return res;
    return PyNumberMethods.super.abs();
  }
}
