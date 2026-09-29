package com.amr.uniVerse.lostfound.service;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.lostfound.dto.CreateLostFoundRequest;
import com.amr.uniVerse.lostfound.dto.LostFoundResponse;
import com.amr.uniVerse.lostfound.entity.ItemStatus;
import com.amr.uniVerse.lostfound.entity.ItemType;
import com.amr.uniVerse.lostfound.entity.LostFoundItem;
import com.amr.uniVerse.lostfound.repository.LostFoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LostFoundService {

    private final LostFoundRepository lostFoundRepository;

    @Transactional
    public LostFoundResponse createItem(CreateLostFoundRequest request, User currentUser) {
        LostFoundItem item = LostFoundItem.builder()
                .title(request.title())
                .description(request.description())
                .type(request.type())
                .status(ItemStatus.OPEN)
                .location(request.location())
                .incidentDate(request.incidentDate())
                .imageUrl(request.imageUrl())
                .reporter(currentUser)
                .build();

        return LostFoundResponse.fromEntity(lostFoundRepository.save(item));
    }

    public List<LostFoundResponse> getAllItems(ItemType type) {
        List<LostFoundItem> items;
        if (type != null) {
            items = lostFoundRepository.findByTypeAndStatusOrderByCreatedAtDesc(type, ItemStatus.OPEN);
        } else {
            items = lostFoundRepository.findByStatusOrderByCreatedAtDesc(ItemStatus.OPEN);
        }
        return items.stream().map(LostFoundResponse::fromEntity).toList();
    }

    public LostFoundResponse getItemById(Long id) {
        LostFoundItem item = lostFoundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        return LostFoundResponse.fromEntity(item);
    }

    @Transactional
    public LostFoundResponse resolveItem(Long id, User currentUser) {
        LostFoundItem item = lostFoundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getReporter().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the reporter of this item");
        }

        item.setStatus(ItemStatus.RESOLVED);
        return LostFoundResponse.fromEntity(lostFoundRepository.save(item));
    }

    @Transactional
    public void deleteItem(Long id, User currentUser) {
        LostFoundItem item = lostFoundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getReporter().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the reporter of this item");
        }

        lostFoundRepository.delete(item);
    }
}