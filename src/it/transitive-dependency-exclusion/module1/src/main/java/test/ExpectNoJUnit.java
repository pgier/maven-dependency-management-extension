package test;

public class ExpectNoJUnit
{
    public static void main( String[] args )
    {
        // Throw exception if the class exists.
        // Have to do it this way because you can't get a successful build out of importing a class that doesn't exist.
        // Have to throw an exception for maven's exec:java, as System.exit() will exit the whole of maven.

        String classFQN = "org.junit.Test";
        try
        {
            Class.forName( classFQN );
            throw new RuntimeException( classFQN + " is available for import."  );
        }
        catch ( ClassNotFoundException e )
        {
        }
    }
}
