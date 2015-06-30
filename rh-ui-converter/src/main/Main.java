package main;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        String fileMask = ".\\*.dfm";
        if (args.length > 0)
            fileMask = args[0];

        RhUiModernizer modernizer = new RhUiModernizer();
        modernizer.run(new File(fileMask));
    }

    /*
     * (non-Java-doc)
     * 
     * @see java.lang.Object#Object()
     */
    public Main() {
        super();
    }

}