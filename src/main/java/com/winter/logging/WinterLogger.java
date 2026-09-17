package com.winter.logging;

public class WinterLogger {

    private boolean enabled;

    private static final String ICE_BLUE = "\u001B[96m";
    private static final String RESET = "\u001B[0m";

    public WinterLogger(boolean enabled) {
        this.enabled = enabled;
    }

    public void log(String message) {

        if (!enabled) {
            return;
        }

        System.out.println(
                ICE_BLUE + "[log] " + RESET + message
        );
    }

    public void startup() {

        if (!enabled) {
            return;
        }

        System.out.println(ICE_BLUE);

        System.out.println(
                "██╗    ██╗██╗███╗   ██╗████████╗███████╗██████╗ "
        );
        System.out.println(
                "██║    ██║██║████╗  ██║╚══██╔══╝██╔════╝██╔══██╗"
        );
        System.out.println(
                "██║ █╗ ██║██║██╔██╗ ██║   ██║   █████╗  ██████╔╝"
        );
        System.out.println(
                "██║███╗██║██║██║╚██╗██║   ██║   ██╔══╝  ██╔══██╗"
        );
        System.out.println(
                "╚███╔███╔╝██║██║ ╚████║   ██║   ███████╗██║  ██║"
        );
        System.out.println(
                " ╚══╝╚══╝ ╚═╝╚═╝  ╚═══╝   ╚═╝   ╚══════╝╚═╝  ╚═╝"
        );

        System.out.println();
        System.out.println("              Winter Framework");
        System.out.println();

        System.out.println(RESET);
    }
}