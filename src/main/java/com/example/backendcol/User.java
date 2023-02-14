package com.example.backendcol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class User {
    //attributes

    //methods



    public Object executeFunction(String function, Integer id){
       try {
           Method method = this.getClass().getMethod(function, id.getClass());
           return method.invoke(this,  id);

       } catch (NoSuchMethodException noSuchMethodException){
           System.out.println(noSuchMethodException);
       } catch (IllegalAccessException illegalAccessException){
           System.out.println(illegalAccessException);
       } catch (InvocationTargetException invocationTargetException){
           System.out.println(invocationTargetException);
       }
       return -1;
    }
}
