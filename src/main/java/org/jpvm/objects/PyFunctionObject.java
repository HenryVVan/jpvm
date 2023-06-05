package org.jpvm.objects;

import org.jpvm.objects.types.PyFunctionType;
import org.jpvm.pycParser.PyCodeObject;
import org.jpvm.python.BuiltIn;

public class PyFunctionObject extends PyObject {

  public static PyObject type = new PyFunctionType();
  public static PyUnicodeObject __name__;
  public static PyDictObject zero = new PyDictObject();
  /**
   * type of {@link PyCodeObject}
   */
  private PyObject funcCode;
  private PyObject funcGlobals;
  private PyObject funcClosure;
  private PyObject funcLocals;
  /**
   * NULL or type of {@link PyTupleObject}
   */
  private PyObject funcDefaults;
  /**
   * NULL or type of {@link PyDictObject}
   */
  private PyObject funcKwDefaults;
  /**
   * only exits if the 1st element of {@link PyCodeObject#getCoConsts()}
   * is {@link PyUnicodeObject} and doc is that.
   * see cpython(3.8.16) funcobject.c:57
   */
  private PyObject funcDoc;
  private PyObject funcName;
  private PyObject funcDict;
  private PyObject funcQualName;
  private PyObject funcModule;
  private PyObject annotation;

  public PyFunctionObject(PyObject funcCode, PyObject funcGlobals, PyObject funcDefaults, PyObject funcKwDefaults, PyObject funcDoc, PyObject funcName, PyObject funcDict, PyObject funcQualName) {
    this.funcCode = funcCode;
    this.funcGlobals = funcGlobals;
    this.funcLocals = new PyDictObject();
    this.funcDefaults = funcDefaults;
    this.funcKwDefaults = funcKwDefaults;
    this.funcDoc = funcDoc;
    this.funcName = funcName;
    this.funcDict = funcDict;
    this.funcQualName = funcQualName;
  }

  public PyFunctionObject(PyCodeObject code, PyDictObject globals,
                          PyUnicodeObject funcQualName) {
    funcCode = code;
    funcGlobals = globals;
    this.funcQualName = funcQualName;
    funcDict = new PyDictObject();
  }

  public static PyBoolObject check(PyObject o) {
    if (type == o)
      return BuiltIn.True;
    return BuiltIn.False;
  }

  public PyObject getFuncCode() {
    return funcCode;
  }

  public void setFuncCode(PyObject funcCode) {
    this.funcCode = funcCode;
  }

  public PyObject getAnnotation() {
    return annotation;
  }

  public void setAnnotation(PyObject annotation) {
    this.annotation = annotation;
  }

  public PyObject getFuncGlobals() {
    return funcGlobals;
  }

  public void setFuncGlobals(PyObject funcGlobals) {
    this.funcGlobals = funcGlobals;
  }

  public PyObject getFuncLocals() {
    return funcLocals;
  }

  public void setFuncLocals(PyObject funcLocals) {
    this.funcLocals = funcLocals;
  }

  public PyObject getFuncDefaults() {
    if (funcDefaults == null)
      funcDefaults = PyTupleObject.getTupleBySize(0);
    return funcDefaults;
  }

  public void setFuncDefaults(PyObject funcDefaults) {
    this.funcDefaults = funcDefaults;
  }

  public PyObject getFuncKwDefaults() {
    if (funcKwDefaults == null)
      return zero;
    return funcKwDefaults;
  }

  public void setFuncKwDefaults(PyObject funcKwDefaults) {
    this.funcKwDefaults = funcKwDefaults;
  }

  public PyObject getFuncDoc() {
    return funcDoc;
  }

  public void setFuncDoc(PyObject funcDoc) {
    this.funcDoc = funcDoc;
  }

  public PyObject getFuncName() {
    return funcName;
  }

  public void setFuncName(PyObject funcName) {
    this.funcName = funcName;
  }

  public PyObject getFuncDict() {
    return funcDict;
  }

  public void setFuncDict(PyObject funcDict) {
    this.funcDict = funcDict;
  }

  public PyObject getFuncQualName() {
    return funcQualName;
  }

  public void setFuncQualName(PyObject funcQualName) {
    this.funcQualName = funcQualName;
  }

  @Override
  public PyObject getType() {
    return type;
  }

  public static void setType(PyObject type) {
    PyFunctionObject.type = type;
  }

  @Override
  public PyUnicodeObject getTypeName() {
    return type.getTypeName();
  }

  @Override
  public PyUnicodeObject str() {
    return new PyUnicodeObject(toString());
  }

  @Override
  public PyUnicodeObject repr() {
    return new PyUnicodeObject(toString());
  }

  @Override
  public String toString() {
    return "PyFunctionObject{" +
        "funcCode=" + funcCode +
        ", funcGlobals=" + funcGlobals +
        ", funcClosure=" + funcClosure +
        ", funcLocals=" + funcLocals +
        ", funcDefaults=" + funcDefaults +
        ", funcKwDefaults=" + funcKwDefaults +
        ", funcDoc=" + funcDoc +
        ", funcName=" + funcName +
        ", funcDict=" + funcDict +
        ", funcQualName=" + funcQualName +
        '}';
  }

  public PyObject getFuncClosure() {
    return funcClosure;
  }

  public void setFuncClosure(PyObject funcClosure) {
    this.funcClosure = funcClosure;
  }

  public PyObject getFuncModule() {
    return funcModule;
  }

  public void setFuncModule(PyObject funcModule) {
    this.funcModule = funcModule;
  }

}
