package nitchcorp.backend.titan.events.Application.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.events.Application.dtos.request.TicketTemplateRequest;
import nitchcorp.backend.titan.events.Application.service.ImplService.TicketTemplateServiceImpl;
import nitchcorp.backend.titan.shared.utils.constantSecurities.SecurityConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Slf4j
@Tag(name = "Votes", description = "API pour la gestion des  ticket")
@RestController
@RequestMapping("/api/ticket_template")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class TicketTemplateController {

    private final TicketTemplateServiceImpl service  ;

    @PostMapping("/create")
    @Operation(summary = "Créer un nouveau  ticket", description = "Crée un nouveau ticket dans le système")
    @ApiResponse(responseCode = "201", description = " Ticket créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> create(@RequestBody TicketTemplateRequest request){
        var response = this.service.create(request) ;
        return new ResponseEntity<>(response  , HttpStatusCode.valueOf(200));
    }


    @PutMapping("/update/{trackingIdTicket}")
    @Operation(summary = " Modifier  un ticket", description = " Modifier un   ticket dans le système")
    @ApiResponse(responseCode = "200", description = " Ticket  modifier avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> update(@PathVariable UUID trackingIdTicket ,
                                    @RequestBody TicketTemplateRequest request){

        var response = this.service.update(request , trackingIdTicket);
        return  new ResponseEntity<>(response  , HttpStatusCode.valueOf(200));

    }


    @GetMapping("/get/{trackingIdTicket}")
    @Operation(summary = " Recuperer  un  ticket", description = " Recuperer un  ticket dans le système")
    @ApiResponse(responseCode = "200", description = " Ticket  recuperé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> get(@PathVariable UUID trackingIdTicket){
            var response = this.service.get(trackingIdTicket) ;
            return  new ResponseEntity<>(response  ,HttpStatusCode.valueOf(200));
    }


    @GetMapping("/all")
    @Operation(summary = " Recuperer une liste de  ticket", description = " Recuperer une liste de ticket dans le système")
    @ApiResponse(responseCode = "200", description = "Liste de ticket  recuperé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> getAll(){

        var response = this.service.getAll();
        return  new ResponseEntity<>(response  , HttpStatus.OK);

    }


    @DeleteMapping("/delete/{trackingIdTicket}")
    @Operation(summary = "  Supprimer un    ticket", description = "  Supprimer un   ticket dans le système")
    @ApiResponse(responseCode = "200", description = " Ticket   supprimé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> delete(@PathVariable UUID trackingIdTicket){
        this.service.delete(trackingIdTicket);
        return  new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/getAllForOne/{trackingIdEvent}")
    @Operation(summary = "   Liste de ticket correspondant a un evenement",
            description = "  Liste de ticket correspondant a un evenement dans le système")
    @ApiResponse(responseCode = "200", description = "Liste de Ticket    recupéré avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> getAllForEvents(@PathVariable UUID trackingIdEvent){

        var response = this.service.getAllForOneEvent(trackingIdEvent);
        return  new ResponseEntity<>(response , HttpStatus.OK);

    }



}
