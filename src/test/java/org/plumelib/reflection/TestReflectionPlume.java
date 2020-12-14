package org.plumelib.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

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
      assertTrue(ReflectionPlume.classForName("int") == int.class);
      assertTrue(ReflectionPlume.classForName("boolean") == boolean.class);
      assertTrue(ReflectionPlume.classForName("java.lang.Class") == Class.class);
      assertTrue(ReflectionPlume.classForName("java.util.Map.Entry") == java.util.Map.Entry.class);
      assertTrue(ReflectionPlume.classForName("java.util.Map$Entry") == java.util.Map.Entry.class);
      assert ReflectionPlume.classForName(
              "org.plumelib.reflection.TestReflectionPlume.Inner.InnerInner")
          == Inner.InnerInner.class;
      assert ReflectionPlume.classForName(
              "org.plumelib.reflection.TestReflectionPlume.Inner$InnerInner")
          == Inner.InnerInner.class;
      boolean success =
          ReflectionPlume.classForName(
                  "org.plumelib.reflection.TestReflectionPlume$Inner.InnerInner")
              == Inner.InnerInner.class;
      assertTrue(success);
      assert ReflectionPlume.classForName(
              "org.plumelib.reflection.TestReflectionPlume$Inner$InnerInner")
          == Inner.InnerInner.class;
    } catch (ClassNotFoundException e) {
      throw new Error(e);
    }
  }

  @Test
  public void test_fullyQualifiedNameToSimpleName() {
    assertEquals("String", ReflectionPlume.fullyQualifiedNameToSimpleName("java.lang.String"));
    assertEquals("String", ReflectionPlume.fullyQualifiedNameToSimpleName("String"));
  }

  @Test
  public void test_nameWithoutPackage() {
    assertEquals("String", ReflectionPlume.nameWithoutPackage(String.class));
    assertEquals("Map.Entry", ReflectionPlume.nameWithoutPackage(java.util.Map.Entry.class));
    assertEquals("TestReflectionPlume.Inner", ReflectionPlume.nameWithoutPackage(Inner.class));
    assertEquals(
        "TestReflectionPlume.Inner.InnerInner",
        ReflectionPlume.nameWithoutPackage(Inner.InnerInner.class));
  }

  @Test
  public void test_isSubtype() {
    // private boolean isSubtype(Class<?> sub, Class<?> sup) {
    assertTrue(ReflectionPlume.isSubtype(Integer.class, Integer.class));
    assertTrue(ReflectionPlume.isSubtype(Cloneable.class, Cloneable.class));
    assertTrue(ReflectionPlume.isSubtype(Object.class, Object.class));
    assertTrue(ReflectionPlume.isSubtype(Integer.class, Number.class));
    assertTrue(!ReflectionPlume.isSubtype(Number.class, Integer.class));
    assertTrue(ReflectionPlume.isSubtype(Integer.class, Comparable.class));
    assertTrue(!ReflectionPlume.isSubtype(Comparable.class, Integer.class));
    assertTrue(ReflectionPlume.isSubtype(Integer.class, Object.class));
    assertTrue(!ReflectionPlume.isSubtype(Object.class, Integer.class));
    assertTrue(!ReflectionPlume.isSubtype(Integer.class, Float.class));
    assertTrue(ReflectionPlume.isSubtype(Collection.class, Iterable.class));
    assertTrue(!ReflectionPlume.isSubtype(Iterable.class, Collection.class));
    assertTrue(ReflectionPlume.isSubtype(ArrayList.class, Iterable.class));
    assertTrue(!ReflectionPlume.isSubtype(Iterable.class, ArrayList.class));
    assertTrue(ReflectionPlume.isSubtype(ArrayList.class, Cloneable.class));
    assertTrue(!ReflectionPlume.isSubtype(Cloneable.class, ArrayList.class));
    assertTrue(ReflectionPlume.isSubtype(ArrayList.class, List.class));
    assertTrue(!ReflectionPlume.isSubtype(List.class, ArrayList.class));
  }

  @Test
  public void test_methodForName() {
    // public static Method methodForName(String methodname) throws ClassNotFoundException
    //
    // Just test that the method is found (return value is non-null and non-erroneous).
    try {
      assertNotNull(
          ReflectionPlume.methodForName(
              "org.plumelib.reflection.ReflectionPlume.methodForName(java.lang.String, java.lang.String, java.lang.Class[])"));
      assertNotNull(
          ReflectionPlume.methodForName(
              "org.plumelib.reflection.ReflectionPlume.methodForName(java.lang.String,java.lang.String,java.lang.Class[])"));
      assertNotNull(ReflectionPlume.methodForName("java.lang.Math.min(int,int)"));
    } catch (Exception e) {
      e.printStackTrace();
      throw new Error(e);
    }
    try {
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
