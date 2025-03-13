package com.arc.arp;

/** Hello world! */
public final class App {

  // Private constructor to prevent instantiation
  private App() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static void main(String[] args) {
    System.out.println("Hello World!");
    System.out.println(addNumbers(1, 2));
  }

  public static boolean addNumbers(int a, int b) {
    System.out.println(a + b);
    return false;
  }
}
