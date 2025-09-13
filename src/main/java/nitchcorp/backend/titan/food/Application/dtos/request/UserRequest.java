package nitchcorp.backend.titan.food.Application.dtos.request;


import lombok.Builder;

@Builder
public record UserRequest(

     String name,

     String surname,

     String email,

     String password

) {}
