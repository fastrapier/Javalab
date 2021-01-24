package ru.itis.app;
import com.beust.jcommander.Parameter;

public class Main {
    @Parameter(names = {"--mode"})
    public String mode;

    @Parameter(names = {"--files"})
    public String files;

    @Parameter(names = {"--count"})
    public int count;

    @Parameter(names = {"--folder"})
    public String folder;
}
