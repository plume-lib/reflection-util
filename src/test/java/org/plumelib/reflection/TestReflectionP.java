package org.plumelib.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test code for the ReflectionP class. */
public final class TestReflectionP {

  static class Inner {
    static class InnerInner {}
  }

  @Test
  void test_classForName() {
    try {
      assertTrue(ReflectionP.classForName("int") == int.class);
      assertTrue(ReflectionP.classForName("boolean") == boolean.class);
      assertTrue(ReflectionP.classForName("java.lang.Class") == Class.class);
      assertTrue(ReflectionP.classForName("java.util.Map.Entry") == java.util.Map.Entry.class);
      assertTrue(ReflectionP.classForName("java.util.Map$Entry") == java.util.Map.Entry.class);
      assert ReflectionP.classForName("org.plumelib.reflection.TestReflectionP.Inner.InnerInner")
          == Inner.InnerInner.class;
      assert ReflectionP.classForName("org.plumelib.reflection.TestReflectionP.Inner$InnerInner")
          == Inner.InnerInner.class;
      boolean success =
          ReflectionP.classForName("org.plumelib.reflection.TestReflectionP$Inner.InnerInner")
              == Inner.InnerInner.class;
      assertTrue(success);
      assert ReflectionP.classForName("org.plumelib.reflection.TestReflectionP$Inner$InnerInner")
          == Inner.InnerInner.class;
    } catch (ClassNotFoundException e) {
      throw new Error(e);
    }
  }

  @Test
  void test_fullyQualifiedNameToSimpleName() {
    assertEquals("String", ReflectionP.fullyQualifiedNameToSimpleName("java.lang.String"));
    assertEquals("String", ReflectionP.fullyQualifiedNameToSimpleName("String"));
  }

  @Test
  void test_nameWithoutPackage() {
    assertEquals("String", ReflectionP.nameWithoutPackage(String.class));
    assertEquals("Map.Entry", ReflectionP.nameWithoutPackage(java.util.Map.Entry.class));
    assertEquals("TestReflectionP.Inner", ReflectionP.nameWithoutPackage(Inner.class));
    assertEquals(
        "TestReflectionP.Inner.InnerInner", ReflectionP.nameWithoutPackage(Inner.InnerInner.class));
  }

  @Test
  void test_isSubtype() {
    // private boolean isSubtype(Class<?> sub, Class<?> sup) {
    assertTrue(ReflectionP.isSubtype(Integer.class, Integer.class));
    assertTrue(ReflectionP.isSubtype(Cloneable.class, Cloneable.class));
    assertTrue(ReflectionP.isSubtype(Object.class, Object.class));
    assertTrue(ReflectionP.isSubtype(Integer.class, Number.class));
    assertTrue(!ReflectionP.isSubtype(Number.class, Integer.class));
    assertTrue(ReflectionP.isSubtype(Integer.class, Comparable.class));
    assertTrue(!ReflectionP.isSubtype(Comparable.class, Integer.class));
    assertTrue(ReflectionP.isSubtype(Integer.class, Object.class));
    assertTrue(!ReflectionP.isSubtype(Object.class, Integer.class));
    assertTrue(!ReflectionP.isSubtype(Integer.class, Float.class));
    assertTrue(ReflectionP.isSubtype(Collection.class, Iterable.class));
    assertTrue(!ReflectionP.isSubtype(Iterable.class, Collection.class));
    assertTrue(ReflectionP.isSubtype(ArrayList.class, Iterable.class));
    assertTrue(!ReflectionP.isSubtype(Iterable.class, ArrayList.class));
    assertTrue(ReflectionP.isSubtype(ArrayList.class, Cloneable.class));
    assertTrue(!ReflectionP.isSubtype(Cloneable.class, ArrayList.class));
    assertTrue(ReflectionP.isSubtype(ArrayList.class, List.class));
    assertTrue(!ReflectionP.isSubtype(List.class, ArrayList.class));
  }

  @Test
  void test_methodForName() {
    // public static Method methodForName(String methodname) throws ClassNotFoundException
    //
    // Just test that the method is found (return value is non-null and non-erroneous).
    try {
      assertNotNull(
          ReflectionP.methodForName(
              "org.plumelib.reflection.ReflectionP.methodForName"
                  + "(java.lang.String, java.lang.String, java.lang.Class[])"));
      assertNotNull(
          ReflectionP.methodForName(
              "org.plumelib.reflection.ReflectionP.methodForName"
                  + "(java.lang.String,java.lang.String,java.lang.Class[])"));
      assertNotNull(ReflectionP.methodForName("java.lang.Math.min(int,int)"));
    } catch (Exception e) {
      e.printStackTrace();
      throw new Error(e);
    }
    try {
      ReflectionP.methodForName("org.plumelib.reflection.ReflectionP.methodForName()");
      throw new Error("Didn't throw NoSuchMethodException");
    } catch (NoSuchMethodException e) {
      // nothing to do; this is the expected case
    } catch (Exception e) {
      e.printStackTrace();
      throw new Error(e);
    }
  }
}
