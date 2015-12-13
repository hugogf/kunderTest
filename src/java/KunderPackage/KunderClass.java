/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package KunderPackage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.*;
/**
 *
 * @author Afra
 */
@Stateless
@Path("/")
public class KunderClass {
    
    @POST
    @Path("/word")
    public Response word(String json) throws JSONException
    {
        try{
            if(!json.isEmpty())
            {
                JSONObject jsonIn = new JSONObject(json);
                String dato;
                
                //Si no es un string ocacionará un erro y devolveremos un
                // response con status 400
                try{
                    dato = jsonIn.getString("data");
                }catch(Exception e){
                    return Response.status(400).entity("Error 400, " + e.getMessage()).build();
                }

                //Variables para validar datos según una exprecion regular
                String expresionValida = "^[a-zA-z]{4}"; //No acepta numeros
                //String expresionValida = "^[a-zA-z, 0-9]{4}"; //Acepta numeros
                Pattern expresionRegular = Pattern.compile(expresionValida);
                Matcher valorEvaluar = expresionRegular.matcher(dato); 
                

                //Valida que solo se entregue un parametro.
                if(jsonIn.length() != 1 ){
                    return Response.status(400).entity("Error 400, solo se acepta 1 parámetro.").build();
                }
                //Valida que el dato entregado cumpla con la expresion regular.
                else if(!valorEvaluar.matches()){
                    return Response.status(400).entity("Error 400, valor entregado no válido.").build();
                }

                JSONObject jsonOut = new JSONObject();
                jsonOut.put("code","00");
                jsonOut.put("description","OK");
                jsonOut.put("data", dato.toUpperCase());

                return Response.ok(jsonOut.toString(), MediaType.APPLICATION_JSON).build();
            }else{
                return Response.status(400).entity("Error 400, no existen parametros de entrada").build();
            }
        }
        catch(Exception e){
            return Response.status(500).entity("Error 500, error interno del servidor." + e.getMessage()).build();
        }
    }
    
    
    @GET
    @Path("/time")
    public Response time(@QueryParam("value") String value) throws JSONException
    {        
        try{
            //Expresion regular valida, modificar en caso de agregar nuevas
            // expresiones regulares.
            String expresionValida = "hora";
            
            //Descomentar si se desea desactivar tipado fuerte para variable value
            // así valores como hora, Hora, hOra, HOra, etc... seran validos
            //value = value.toUpperCase();
            //expresionValida = expresionValida.toUpperCase();
            
            //Define el formato que debe tener la variable value
            Pattern expresionRegular = Pattern.compile(expresionValida);
            Matcher valorEvaluar = expresionRegular.matcher(value);
            String mensaje = "";
            
            if(valorEvaluar.matches())
            {
                //Obtenemos la fecha en formato UTC
                Date fecha = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                
                //Se crea el JSON de respuesta
                JSONObject rs = new JSONObject();
                rs.put("code", "00");
                rs.put("description", "OK");
                rs.put("data", sdf.format(fecha));
                
                // Se retorna una respuesta con el json y el valor status 200
                return Response.ok(rs.toString(), MediaType.APPLICATION_JSON).build();
            }
            // Error si es que la variable no fue enviada o fue enviada en blanco.
            else if(value.isEmpty())
            {
                mensaje = "Error 400, debe especificar la variable 'value'.";
            }
            //Error si variable contiene algun valor no aceptado.
            else if(!value.isEmpty())
            {
                mensaje = "Error 400, valor '"+value+"' no soportado.";
            }
            return Response.status(400).entity(mensaje).build();
        }
        //Retorna error 500 si ocurre algo en tiempo de ejecución.
        catch(Exception e){
            return Response.serverError().entity(e).build();
        }
    }
}
