package com.example.backendcol;

public class RequestsParameters {

    private Integer id;
    private String function;

    public RequestsParameters(){
        this.id = -1;
        this.function = "";
    }

    public void setID(Integer inputID){
        this.id = inputID;
    }

    public void setFunction(String inputFunction){
        this.function = inputFunction;
    }


    public Integer getID(){
        return this.id;
    }

    public String getFunction(){
        return this.function;
    }

}
