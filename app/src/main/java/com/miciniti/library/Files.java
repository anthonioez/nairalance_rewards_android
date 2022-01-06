package com.miciniti.library;

import java.io.File;

public class Files
{
    public static void deleteFolder(File files)
    {
        if (files.isDirectory())
        {
            for (File child : files.listFiles())
            {
                deleteFolder(child);
            }
        }

        files.delete();
    }
}

