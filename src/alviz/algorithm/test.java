import java.io.*;
import java.util.*;

class MyPair
{
    private final int key;
    private final int value;

    public MyPair(int aKey, int aValue)
    {
        key   = aKey;
        value = aValue;
    }

    public int key()   { return key; }
    public int value() { return value; }
}
 

class test
{
    public static void main(String[] args)
        throws IOException
    {
        // size of ArrayList
        int n = 5;
         //declaring ArrayList with initial size n
        ArrayList<MyPair> arrli = new ArrayList<MyPair>();
 
        // Appending the new element at the end of the list
        MyPair mp;
        for (int i=1; i<=n; i++){
            mp = new MyPair(i,i+1);
            arrli.add(mp);
         }
            
        // Printing elements
        System.out.println(arrli);
    }
}
