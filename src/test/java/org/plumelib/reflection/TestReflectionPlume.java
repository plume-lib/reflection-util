package org.plumelib.reflection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;

/** Test code for the ReflectionPlume class. */
@SuppressWarnings({
  "UseCorrectAssertInTests" // I don't see the problem with using `assert`
})
public final class TestReflectionPlume {

  static class Inner {
    static class InnerInner {}
  }

  @Test
  public void test_classForName() {
    try {
      assert ReflectionPlume.classForName("int") == int.class;
      assert ReflectionPlume.classForName("boolean") == boolean.class;
      assert ReflectionPlume.classForName("java.lang.Class") == Class.class;
      assert ReflectionPlume.classForName("java.util.Map.Entry") == java.util.Map.Entry.class;
      assert ReflectionPlume.classForName("java.util.Map$Entry") == java.util.Map.Entry.class;
      assert ReflectionPlume.classForName(
              "org.plumelib.reflection.TestReflectionPlume.Inner.InnerInner")
          == Inner.InnerInner.class;
      assert ReflectionPlume.classForName(
              "org.plumelib.reflection.TestReflectionPlume.Inner$InnerInner")
          == Inner.InnerInner.class;
      @SuppressWarnings("signature") // argument is illegal, but routine works anyway
      boolean success =
          ReflectionPlume.classForName(
                  "org.plumelib.reflection.TestReflectionPlume$Inner.InnerInner")
              == Inner.InnerInner.class;
      assert success;
      assert ReflectionPlume.classForName(
              "org.plumelib.reflection.TestReflectionPlume$Inner$InnerInner")
          == Inner.InnerInner.class;
    } catch (ClassNotFoundException e) {
      throw new Error(e);
    }
  }

  @Test
  public void test_fullyQualifiedNameToSimpleName() {
    assert ReflectionPlume.fullyQualifiedNameToSimpleName("java.lang.String").equals("String");
    assert ReflectionPlume.fullyQualifiedNameToSimpleName("String").equals("String");
  }

  @Test
  public void test_isSubtype() {
    // private boolean isSubtype(Class<?> sub, Class<?> sup) {
    assert ReflectionPlume.isSubtype(Integer.class, Integer.class);
    assert ReflectionPlume.isSubtype(Cloneable.class, Cloneable.class);
    assert ReflectionPlume.isSubtype(Object.class, Object.class);
    assert ReflectionPlume.isSubtype(Integer.class, Number.class);
    assert !ReflectionPlume.isSubtype(Number.class, Integer.class);
    assert ReflectionPlume.isSubtype(Integer.class, Comparable.class);
    assert !ReflectionPlume.isSubtype(Comparable.class, Integer.class);
    assert ReflectionPlume.isSubtype(Integer.class, Object.class);
    assert !ReflectionPlume.isSubtype(Object.class, Integer.class);
    assert !ReflectionPlume.isSubtype(Integer.class, Float.class);
    assert ReflectionPlume.isSubtype(Collection.class, Iterable.class);
    assert !ReflectionPlume.isSubtype(Iterable.class, Collection.class);
    assert ReflectionPlume.isSubtype(ArrayList.class, Iterable.class);
    assert !ReflectionPlume.isSubtype(Iterable.class, ArrayList.class);
    assert ReflectionPlume.isSubtype(ArrayList.class, Cloneable.class);
    assert !ReflectionPlume.isSubtype(Cloneable.class, ArrayList.class);
    assert ReflectionPlume.isSubtype(ArrayList.class, List.class);
    assert !ReflectionPlume.isSubtype(List.class, ArrayList.class);
  }

  @Test
  public void test_methodForName() {
    // public static Method methodForName(String methodname) throws ClassNotFoundException
    //
    // Just test that the method is found (return value is non-null and non-erroneous).
    try {
      assert null
          != ReflectionPlume.methodForName(
              "org.plumelib.reflection.ReflectionPlume.methodForName(java.lang.String, java.lang.String, java.lang.Class[])");
      assert null
          != ReflectionPlume.methodForName(
              "org.plumelib.reflection.ReflectionPlume.methodForName(java.lang.String,java.lang.String,java.lang.Class[])");
      assert null != ReflectionPlume.methodForName("java.lang.Math.min(int,int)");
    } catch (Exception e) {
      e.printStackTrace();
      throw new Error(e);
    }
    try {
      java.lang.reflect.Method m =
          ReflectionPlume.methodForName("org.plumelib.reflection.ReflectionPlume.methodForName()");
      throw new Error("Didn't throw NoSuchMethodException");
    } catch (NoSuchMethodException e) {
      // nothing to do; this is the expected case
    } catch (Exception e) {
      e.printStackTrace();
      throw new Error(e);
    }
  }
}
