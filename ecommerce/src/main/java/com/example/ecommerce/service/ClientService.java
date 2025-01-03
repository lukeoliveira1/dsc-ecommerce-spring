package com.example.ecommerce.service;

import com.example.ecommerce.domain.Client;
import com.example.ecommerce.domain.dto.address.AddressRequestDTO;
import com.example.ecommerce.domain.dto.client.ClientRequestDTO;
import com.example.ecommerce.domain.dto.client.ClientResponseDTO;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.AddressMapper;
import com.example.ecommerce.mapper.ClientMapper;
import com.example.ecommerce.repository.AddressRepository;
import com.example.ecommerce.repository.ClientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private AddressRepository addressRepository;

    public ClientResponseDTO save(ClientRequestDTO body) {
        var client = clientMapper.toEntity(body);

        if (clientRepository.existsByName(body.name())) {
            throw new BusinessException("Já existe um cliente com esse nome");
        }
        if (clientRepository.existsByEmail(body.email())) {
            throw new BusinessException("Já existe um cliente com esse email");
        }
        // validar CPF...

        try {
            clientRepository.save(client);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("CPF já cadastrado no sistema!");
        }

        return clientMapper.toResponseDTO(client);
    }

    public Page<ClientResponseDTO> list(Pageable pageable) {
        Page<Client> clientsPage = clientRepository.findAll(pageable);

        return clientsPage.map(clientMapper::toResponseDTO);
    }

    public ClientResponseDTO getById(Long id) {
        var client = clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        return clientMapper.toResponseDTO(client);
    }

    public ClientResponseDTO update(Long id, ClientRequestDTO body) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        if (!client.getName().equals(body.name()) && clientRepository.existsByName(body.name())) {
            throw new BusinessException("Já existe um cliente com esse nome");
        }
        if (clientRepository.existsByEmail(body.email())) {
            throw new BusinessException("Já existe um cliente com esse email");
        }

        clientMapper.updateEntityFromDTO(body, client);

        var updatedClient = clientRepository.save(client);

        return clientMapper.toResponseDTO(updatedClient);
    }

    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado");
        }

        Client client = clientRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cliente não encontrado!")
        );

        if (!client.getPurchase().isEmpty()) {
            throw new BusinessException("Cliente não pode ser removido se " +
                    "possuir pedidos!");
        }

        clientRepository.deleteById(id);
    }

    public ClientResponseDTO addAddressInClient(Long id, AddressRequestDTO body) {
        var address = addressMapper.toEntity(body);

        var client = clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        client.setAddress(address);
        address.setClient(client);

        addressRepository.save(address);
        clientRepository.save(client);

        return clientMapper.toResponseDTO(client);
    }

    public ClientResponseDTO getAddressOfClient(Long id) {
        var client = clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        return clientMapper.toResponseDTO(client);
    }

    public ClientResponseDTO updateAddressOfClient(Long id, AddressRequestDTO dto) {
        var client = clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        var address = client.getAddress();

        addressMapper.updateEntityFromDTO(dto, address);
        addressRepository.save(address);

        return clientMapper.toResponseDTO(client);
    }

    public void deleteAddressOfClient(Long id) {
        var client = clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        var address = client.getAddress();

        addressRepository.delete(address);
    }

//    public List<PurchaseResponseDTO> listPurchases(Long id) {}

}
