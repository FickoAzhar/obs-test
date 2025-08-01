package com.example.obs_test.service;

import com.example.obs_test.dto.ItemDto;
import com.example.obs_test.dto.ItemRequest;
import com.example.obs_test.entity.Item;
import com.example.obs_test.exception.DataNotFoundException;
import com.example.obs_test.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public Page<ItemDto> getAllItemsWithStock(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(this::convertItemToDto);
    }

    public ItemDto save(ItemRequest request) {
        Item item = itemRepository.save(Item.builder().name(request.getName()).price(request.getPrice()).build());
        return convertItemToDto(item);
    }

    public ItemDto findById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(id));
        return convertItemToDto(item);
    }

    public ItemDto update(Long id, ItemRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(id));

        item.setName(request.getName());
        item.setPrice(request.getPrice());
        itemRepository.save(item);
        return convertItemToDto(item);
    }

    public void delete(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(id));
        itemRepository.delete(item);
    }

    ItemDto convertItemToDto(Item item) {
        if (item == null)
            return null;

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .remainingStock(item.getRemainingStock())
                .build();
    }
}

