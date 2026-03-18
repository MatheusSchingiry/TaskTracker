package com.tasktracker.cli;

import com.tasktracker.cli.dto.ProjectDTO;
import com.tasktracker.cli.dto.TaskDTO;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskTrackerCLI {

    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE   = "\u001B[34m";
    private static final String RED    = "\u001B[31m";
    private static final String CYAN   = "\u001B[36m";
    private static final String WHITE  = "\u001B[97m";

    private final ApiClient apiClient = new ApiClient();

    private ScheduledExecutorService scheduler;

    public void start() {
        renderDashboard();
        startAutoRefresh();
        startInputLoop();
    }

    private void startAutoRefresh() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                this::renderDashboard,
                30,
                30,
                TimeUnit.SECONDS
        );
    }

    private void renderDashboard() {
        clearScreen();
        printHeader();

        List<ProjectDTO> projects = apiClient.fetchProjects();
        printSummary(projects);
        printProjectsTable(projects);
        printFooterMenu();
    }

    private void printHeader() {
        System.out.println(CYAN + "╔══════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║ " + WHITE + BOLD + "████████╗ █████╗ ███████╗██╗  ██╗                       " + RESET + CYAN + "║" + RESET);
        System.out.println(CYAN + "║ " + WHITE + BOLD + "   ██╔══╝██╔══██╗██╔════╝██║ ██╔╝                       " + RESET + CYAN + "║" + RESET);
        System.out.println(CYAN + "║ " + WHITE + BOLD + "   ██║   ███████║███████╗█████╔╝                        " + RESET + CYAN + "║" + RESET);
        System.out.println(CYAN + "║ " + WHITE + BOLD + "   ██║   ██╔══██║╚════██║██╔═██╗                        " + RESET + CYAN + "║" + RESET);
        System.out.println(CYAN + "║ " + WHITE + BOLD + "   ██║   ██║  ██║███████║██║  ██╗                       " + RESET + CYAN + "║" + RESET);
        System.out.println(CYAN + "║ " + WHITE + BOLD + "   ╚═╝   ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝ " + RESET + CYAN + "TaskTracker v1.0  ║" + RESET);
        System.out.println(CYAN + "╚══════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    private void printSummary(List<ProjectDTO> projects) {
        long total      = projects.size();
        long ativos     = projects.stream().filter(p -> "ACTIVE".equals(p.getStatus())).count();
        long concluidos = projects.stream().filter(p -> "COMPLETED".equals(p.getStatus())).count();

        System.out.printf(BOLD + "[PROJETOS]" + RESET +
                "  Total: " + CYAN + "%d" + RESET +
                "   Ativos: " + GREEN + "%d" + RESET +
                "   Concluídos: " + BLUE + "%d%n" + RESET,
                total, ativos, concluidos);
        printDivider();
    }

    private void printProjectsTable(List<ProjectDTO> projects) {
        System.out.printf(BOLD + "%-4s %-25s %-14s %-12s%n" + RESET,
                "#", "PROJETO", "STATUS", "CRIADO EM");
        printDivider();

        if (projects.isEmpty()) {
            System.out.println("  Nenhum projeto encontrado. Verifique se a API está rodando.");
        } else {
            for (int i = 0; i < projects.size(); i++) {
                ProjectDTO p = projects.get(i);
                System.out.printf("%-4d %-25s ", i + 1, truncate(p.getNome(), 24));
                System.out.printf("%-24s", colorizeStatus(p.getStatus()));
                System.out.printf("%-12s%n", formatDate(p.getCriadoEm()));
            }
        }
        printDivider();
    }

    private void printFooterMenu() {
        System.out.println();
        System.out.print(BOLD + "[q]" + RESET + " Sair   ");
        System.out.print(BOLD + "[r]" + RESET + " Atualizar   ");
        System.out.println(BOLD + "[p]" + RESET + " Ver tasks de um projeto");
        System.out.print("> ");
    }

    private void startInputLoop() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "q" -> {
                    scheduler.shutdownNow();
                    System.out.println(YELLOW + "\nSaindo do TaskTracker..." + RESET);
                    System.exit(0);
                }
                case "r" -> renderDashboard();

                case "p" -> {
                    scheduler.shutdownNow();
                    showTasksMenu(scanner);
                    renderDashboard();
                    startAutoRefresh();
                }

                default -> {
                    System.out.println(RED + "Comando inválido. Use [q], [r] ou [p]." + RESET);
                    System.out.print("> ");
                }
            }
        }
    }

    private void showTasksMenu(Scanner scanner) {
        clearScreen();
        List<ProjectDTO> projects = apiClient.fetchProjects();

        System.out.println(CYAN + BOLD + "═══ SELECIONE UM PROJETO ═══" + RESET);
        System.out.println();

        for (int i = 0; i < projects.size(); i++) {
            System.out.printf("  " + BOLD + "[%d]" + RESET + " %s%n", i + 1, projects.get(i).getNome());
        }
        System.out.println("  " + BOLD + "[0]" + RESET + " Voltar");
        System.out.println();
        System.out.print("> ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice == 0) return;

            if (choice < 1 || choice > projects.size()) {
                System.out.println(RED + "Opção inválida." + RESET);
                return;
            }

            showProjectTasks(projects.get(choice - 1));

        } catch (NumberFormatException e) {
            System.out.println(RED + "Digite um número válido." + RESET);
        }

        System.out.println();
        System.out.println(YELLOW + "Pressione ENTER para voltar..." + RESET);
        scanner.nextLine();
    }

    private void showProjectTasks(ProjectDTO project) {
        clearScreen();
        System.out.println(CYAN + BOLD + "═══ TASKS: " + project.getNome().toUpperCase() + " ═══" + RESET);
        System.out.println();

        List<TaskDTO> tasks = apiClient.fetchTasks(project.getId());

        if (tasks.isEmpty()) {
            System.out.println("  Nenhuma task encontrada para este projeto.");
            return;
        }

        System.out.printf(BOLD + "%-4s %-30s %-16s %-12s%n" + RESET,
                "#", "TASK", "STATUS", "CRIADO EM");
        printDivider();

        for (int i = 0; i < tasks.size(); i++) {
            TaskDTO t = tasks.get(i);
            System.out.printf("%-4d %-30s ", i + 1, truncate(t.getNome(), 29));
            System.out.printf("%-27s", colorizeStatus(t.getStatus()));
            System.out.printf("%-12s%n", formatDate(t.getCriadoEm()));
        }
        printDivider();
    }

    private String colorizeStatus(String status) {
        if (status == null) return "-";
        return switch (status) {
            case "ACTIVE"       -> GREEN  + "[ACTIVE]"      + RESET;
            case "PAUSED"       -> YELLOW + "[PAUSED]"      + RESET;
            case "COMPLETED"    -> BLUE   + "[COMPLETED]"   + RESET;
            case "CANCELLED"    -> RED    + "[CANCELLED]"   + RESET;
            case "PENDING"      -> WHITE  + "[PENDING]"     + RESET;
            case "IN_PROGRESS"  -> CYAN   + "[IN PROG.]"    + RESET;
            default             -> status;
        };
    }

    private String formatDate(String timestamp) {
        if (timestamp == null) return "-";
        return timestamp.length() >= 10 ? timestamp.substring(0, 10) : timestamp;
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void printDivider() {
        System.out.println("─".repeat(60));
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
