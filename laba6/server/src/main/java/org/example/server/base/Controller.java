package org.example.server.base;

import org.example.server.commandIn.*;
import org.example.server.data.City;
import org.example.server.commandIn.*;

import java.io.FileNotFoundException;

public class Controller {

    private String js = "";
    private int i = 1;
    //private static final Gson Gson = new GsonBuilder().setPrettyPrinting().create();
    CommandControl control =new CommandControl();
    public static String str ="";
    Converter converter =new Converter();
    ReadLine readLine =new ReadLine();
    public void createCommand(){
        control.addCommand("help",new Help());
        control.addCommand("exit",new Exit());
        control.addCommand("info",new Info());
        control.addCommand("show",new Show());
        control.addCommand("clear",new Clear());
        control.addCommand("min_by_capital",new MinByCapital());
        control.addCommand("save",new Save());
        control.addCommand("remove_head",new RemoveHead());
        control.addCommand("head",new Head());
        control.addCommand("count_less_than_climate",new CountLessThanClimate());
        control.addCommand("filter_starts_with_name",new FilterStartsWithName());
        control.addCommand("remove_by_id",new RemoveById());
        control.addCommand("execute_script",new ExecuiteScript());
        control.addCommand("add",new Add());
        control.addCommand("add_if_max",new AddIfMax());
        control.addCommand("update",new Update());
        WorkFile workFile =new WorkFile();
        try {
            String tip = workFile.reader(str);
            CollectionManager.cities =(converter.cityFromJson(tip));
        }catch (NullPointerException e){

        }

    }
    public String run(String command, City city, String line) throws FileNotFoundException {

        command =command.toLowerCase();

        switch (command) {
            case "info","help","exit","show","clear","save","head","remove_head","min_by_capital":

                return (control.getCommand(command).execution(command,city));

            case "remove_by_id","update":
                return (control.getCommand(command).execution(command,city));

            case "add_if_max","add":

                return (control.getCommand(command).execution(command,city));

            case "filter_starts_with_name":
                return (control.getCommand(command).execution(line,city));
            case "execute_script":
//                System.out.println(control.getCommand(command).execution(line,null));
                break;
            case "count_less_than_climate":
                return (control.getCommand(command).execution(line,city));

            default:

                break;


        }
        return "";
    }


}
