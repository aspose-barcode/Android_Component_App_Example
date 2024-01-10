import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class GradleAssist
{
    private static String TAG = "## GradleAssist ->";

    public static void sayHi()
    {
        System.out.println("HI");
    }

    public static Properties readProperties(String filePath)
    {
        Properties properties = new Properties();
        try
        {
            File file = new File(filePath);
            if (file.exists())
            {
                System.out.println(TAG + " file " + file.getAbsolutePath() + " exists");
                properties.load(Files.newInputStream(file.toPath()));
            }
            else
            {
                System.out.println(TAG + " file " + file.getAbsolutePath() + " absent");
            }
        }
        catch (Exception e)
        {
            System.out.println(TAG + e.getMessage());
        }
        return properties;
    }
}
