package nitchcorp.backend.titan.events.Application.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.events.Application.dtos.request.VotesRequest;
import nitchcorp.backend.titan.events.Application.service.VotesService;
import nitchcorp.backend.titan.shared.utils.constantSecurities.SecurityConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Tag(name = "Votes", description = "API pour la gestion des votes")
@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class VoteController {


    private final VotesService service ;


    @PostMapping("/create")
    @Operation(summary = "Créer un nouveau vote", description = "Crée un nouveau vote dans le système")
    @ApiResponse(responseCode = "201", description = "Vote créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> create(@RequestBody VotesRequest request){
        var response  = this.service.create(request) ;
        return new ResponseEntity<>(response , HttpStatusCode.valueOf(200));
    }

    @GetMapping("/get/{trackingIdVote}")
    @Operation(summary = "Recupérer un vote avec son tracking id",
            description = "Recupérer un vote dans le système ")
    @ApiResponse(responseCode = "200", description = "Vote recupéré avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> get(@PathVariable UUID trackingIdVote){
        var response = this.service.getByTrackingId(trackingIdVote) ;
        return new ResponseEntity<>(response , HttpStatusCode.valueOf(200));
    }


    @PutMapping("/update/{trackingIdVote}")
    @Operation(summary = "Modifier un vote avec son tracking id et la donnée de modification",
            description = "Modifier un vote dans le système ")
    @ApiResponse(responseCode = "200", description = "Vote  modifié avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> update(@PathVariable UUID trackingIdVote ,
                                    @RequestBody VotesRequest request){
        var response = this.service.update(request , trackingIdVote) ;

        return  new ResponseEntity<>(response , HttpStatusCode.valueOf(200));

    }

    @DeleteMapping("/delete/{trackingIdVote}")
    @Operation(summary = "Supprimer un vote avec son tracking id",
            description = "Supprimer un vote dans le système ")
    @ApiResponse(responseCode = "200", description = "Vote   supprimé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> delete(@PathVariable UUID trackingIdVote){
            this.service.delete(trackingIdVote);
            return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }

    @GetMapping("/all")
    @Operation(summary = " Récupérer la liste des votes  ",
            description = "  Récupérer la liste des votes un vote dans le système ")
    @ApiResponse(responseCode = "200", description = "Vote  la liste des votes avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> getAll(){
        var response = this.service.listVotes() ;
        return  new ResponseEntity<>(response , HttpStatusCode.valueOf(200));
    }

    @GetMapping("/allForOneEvent/{trackingIdEvent}")
    @Operation(summary = " Récupérer la liste des votes  ",
            description = "  Récupérer la liste des votes un vote dans le système ")
    @ApiResponse(responseCode = "200", description = "Vote  la liste des votes avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public  ResponseEntity<?> getAllForOneEvent(@PathVariable UUID trackingIdEvent){
        var response = this.service.listForOneEvent(trackingIdEvent) ;
        return new ResponseEntity<>(response , HttpStatus.OK) ;
    }


}
