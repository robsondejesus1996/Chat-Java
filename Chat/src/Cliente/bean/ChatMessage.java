/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.xml.ws.Action;

/**
 *
 * @author Robson 
 */
public class ChatMessage implements Serializable{
    
    
    private String name;
    private String text;
    private String nameReserved; // o nome do cliente que a mensagem foi reservada
    private Set<String> setOnlines = new HashSet<String>();//lista de usuários onlines
    private Action action;//Para cada mensagem que o cliente envia para o servidor, ele vai dizer qual é a ação que ele quer executar

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNameReserved() {
        return nameReserved;
    }

    public void setNameReserved(String nameReserved) {
        this.nameReserved = nameReserved;
    }

    public Set<String> getSetOnlines() {
        return setOnlines;
    }

    public void setSetOnlines(Set<String> setOnlines) {
        this.setOnlines = setOnlines;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
    

    //acoes a serem executadas. conexao, sair do chat, mensagem reservada, mensagem para todos os usuários, usuários onlines
    public enum Action {
        CONNECT, DISCONNETCT, SEND_ONE, SEND_ALL, USERS_ONLINE  
    }
    
}
