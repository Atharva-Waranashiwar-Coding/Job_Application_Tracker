package com.example.jobtracker.controller;

import com.example.jobtracker.dto.*;
import com.example.jobtracker.service.ContactService;
import com.example.jobtracker.service.DocumentService;
import com.example.jobtracker.service.NoteService;
import com.example.jobtracker.service.OfferService;
import com.example.jobtracker.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications/{applicationId}")
public class ApplicationDetailsController {

    private final DocumentService documentService;
    private final NoteService noteService;
    private final ContactService contactService;
    private final OfferService offerService;

    public ApplicationDetailsController(DocumentService documentService,
                                        NoteService noteService,
                                        ContactService contactService,
                                        OfferService offerService) {
        this.documentService = documentService;
        this.noteService = noteService;
        this.contactService = contactService;
        this.offerService = offerService;
    }

    @GetMapping("/documents")
    public ApplicationDocumentsDto getDocuments(@PathVariable Long applicationId) {
        return documentService.getForApplication(SecurityUtil.getCurrentUsername(), applicationId);
    }

    @PutMapping("/documents")
    public ApplicationDocumentsDto updateDocuments(@PathVariable Long applicationId,
                                                   @Valid @RequestBody UpdateApplicationDocumentsRequest request) {
        return documentService.updateForApplication(SecurityUtil.getCurrentUsername(), applicationId, request);
    }

    @GetMapping("/notes")
    public List<ApplicationNoteDto> getNotes(@PathVariable Long applicationId) {
        return noteService.listForApplication(SecurityUtil.getCurrentUsername(), applicationId);
    }

    @PutMapping("/notes")
    public ApplicationNoteDto upsertNote(@PathVariable Long applicationId,
                                         @Valid @RequestBody UpsertApplicationNoteRequest request) {
        return noteService.upsert(SecurityUtil.getCurrentUsername(), applicationId, request);
    }

    @GetMapping("/contacts")
    public List<ApplicationContactDto> getContacts(@PathVariable Long applicationId) {
        return contactService.listForApplication(SecurityUtil.getCurrentUsername(), applicationId);
    }

    @PostMapping("/contacts")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationContactDto createContact(@PathVariable Long applicationId,
                                               @Valid @RequestBody UpsertApplicationContactRequest request) {
        return contactService.create(SecurityUtil.getCurrentUsername(), applicationId, request);
    }

    @PutMapping("/contacts/{contactId}")
    public ApplicationContactDto updateContact(@PathVariable Long applicationId,
                                               @PathVariable Long contactId,
                                               @Valid @RequestBody UpsertApplicationContactRequest request) {
        return contactService.update(SecurityUtil.getCurrentUsername(), applicationId, contactId, request);
    }

    @DeleteMapping("/contacts/{contactId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContact(@PathVariable Long applicationId, @PathVariable Long contactId) {
        contactService.delete(SecurityUtil.getCurrentUsername(), applicationId, contactId);
    }

    @GetMapping("/offers")
    public List<OfferDto> getOffers(@PathVariable Long applicationId) {
        return offerService.listForApplication(SecurityUtil.getCurrentUsername(), applicationId);
    }

    @PostMapping("/offers")
    @ResponseStatus(HttpStatus.CREATED)
    public OfferDto createOffer(@PathVariable Long applicationId, @Valid @RequestBody UpsertOfferRequest request) {
        return offerService.create(SecurityUtil.getCurrentUsername(), applicationId, request);
    }

    @PutMapping("/offers/{offerId}")
    public OfferDto updateOffer(@PathVariable Long applicationId,
                                @PathVariable Long offerId,
                                @Valid @RequestBody UpsertOfferRequest request) {
        return offerService.update(SecurityUtil.getCurrentUsername(), applicationId, offerId, request);
    }

    @DeleteMapping("/offers/{offerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOffer(@PathVariable Long applicationId, @PathVariable Long offerId) {
        offerService.delete(SecurityUtil.getCurrentUsername(), applicationId, offerId);
    }
}
