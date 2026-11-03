package com.david.escuela.mappers;
//recibre el request(RQ), la entidad(E) y la response(RS)
public interface CommonMapper <RQ,E,RS>{

    E requestAEntidad(RQ request);

    RS entidadAResponse(E entidad);

}
