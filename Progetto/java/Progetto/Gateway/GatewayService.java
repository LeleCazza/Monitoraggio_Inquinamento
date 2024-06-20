package Progetto.Gateway;

import Progetto.Eccezioni.ErroreMancanzaNstatistiche;
import Progetto.Nodo.Nodo;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("gateway")
public class GatewayService {

    @Path("listaNodi")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getListaNodi(){
        return Response.ok(Gateway.getInstance().getListaNodi()).build();
    }

    @Path("NumeroNodi")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getNumeroNodi(){
        return Response.ok(Gateway.getInstance().getNumeroNodi()).build();
    }

    @Path("addNodo")
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response addNodo(Nodo nodo){
        int riuscito = Gateway.getInstance().addNodo(nodo);
        if(riuscito!=0)
            return Response.ok(riuscito).build();
        else
            return Response.status(Response.Status.CONFLICT).build();
    }

    @Path("addMedia")
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response addMedia(Mediaquartiere mediaQuartiere){
        boolean riuscito = Gateway.getInstance().addMedia(mediaQuartiere);
        if(riuscito)
            return Response.ok().build();
        else
            return Response.serverError().build();
    }

    @Path("rmNodo")
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response removeNodo(Nodo nodo){
        boolean riuscito = Gateway.getInstance().removeNodo(nodo);
        if(riuscito)
            return Response.ok().build();
        else
            return Response.serverError().build();
    }

    @Path("getNstatistiche")
    @POST
    @Consumes({"text/plain"})
    @Produces({"application/json", "application/xml"})
    public Response getUltimeStatistiche(String n){
        try {
            return Response.ok(Gateway.getInstance().getUltimeStatistiche(n)).build();
        } catch (ErroreMancanzaNstatistiche e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }

    @Path("getNdev")
    @POST
    @Consumes({"text/plain"})
    @Produces({"application/json", "application/xml"})
    public Response getUltimeDev(String n){
        try {
            return Response.ok(Gateway.getInstance().getUltimeDev(n)).build();
        } catch (ErroreMancanzaNstatistiche e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }
}