/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.entities;

/**
 *
 * @author Welcome
 */
public enum Token {
    BEGIN_ARRAY("begin_arr"), END_ARRAY("end_arr"), BEGIN_STRING("begin_str"), END_STRING("end_str");
    
    public static final int LENGTH_CHECK = 0;
    
    private final String token;

    private Token(final String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
