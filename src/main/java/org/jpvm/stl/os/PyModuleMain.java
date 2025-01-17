package org.jpvm.stl.os;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.jpvm.errors.PyException;
import org.jpvm.excptions.PyErrorUtils;
import org.jpvm.objects.*;
import org.jpvm.objects.annotation.PyClassAttribute;
import org.jpvm.objects.annotation.PyClassMethod;
import org.jpvm.pvm.InterpreterState;
import org.jpvm.pvm.PVM;

public class PyModuleMain extends PyModuleObject {

  @PyClassAttribute PyObject path = new PyPath(new PyUnicodeObject("path"));

  public PyModuleMain(PyUnicodeObject moduleName) {
    super(moduleName);
  }

  @PyClassMethod
  public PyObject listdir(PyTupleObject args, PyDictObject kwArgs) throws PyException {
    Path path = null;
    if (args.size() == 1) {
      path = Path.of(args.get(0).toString());
    } else if (args.size() == 0) {
      path = Path.of(".");
    }
    if (path != null) {
      InterpreterState is = PVM.getThreadState().getIs();
      is.dropGIL();
      try (Stream<Path> list = Files.list(path)) {
        PyListObject res = new PyListObject();
        list.forEach(x -> res.append(new PyUnicodeObject(x.getFileName().toString())));
        return res;
      } catch (IOException e) {
        return PyErrorUtils.pyErrorFormat(
            PyErrorUtils.FileNotFoundError, "can not find a dir named " + args.get(0));
      } finally {
        is.takeGIL();
      }
    }
    PyErrorUtils.pyErrorFormat(
        PyErrorUtils.TypeError, "listdir requires exactly one or no argument");
    return null;
  }
}
