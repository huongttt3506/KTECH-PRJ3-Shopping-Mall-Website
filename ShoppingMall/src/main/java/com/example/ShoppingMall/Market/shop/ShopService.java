package com.example.ShoppingMall.Market.shop;

import com.example.ShoppingMall.AuthenticationFacade;
import com.example.ShoppingMall.Market.shop.dto.*;
import com.example.ShoppingMall.Market.shop.entity.*;
import com.example.ShoppingMall.Market.shop.repo.ShopCloseRepository;
import com.example.ShoppingMall.Market.shop.repo.ShopRegRepository;
import com.example.ShoppingMall.Market.shop.repo.ShopRepository;
import com.example.ShoppingMall.user.dto.UserDto;
import com.example.ShoppingMall.user.entity.UserEntity;
import com.example.ShoppingMall.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRegRepository shopRegRepository;
    private final ShopRepository shopRepository;
    private final ShopCloseRepository shopCloseRepository;
    private final AuthenticationFacade facade;

    // After admin accepted business registration
    // New shop was created with status: PREPARING
    // User Business can update some shop info, ex: name, description, category

    //Read All Shops by Admin and Read All Shops by Owner
    public List<ShopDto> getAllShops() {
        // Get the current user from AuthenticationFacade
        UserEntity userEntity = facade.getCurrentUserEntity();

        // Check if the user is admin
        if (userEntity.getRole().equals(UserRole.ROLE_ADMIN)) {
            // Admin can view all shops
            List<ShopEntity> shops = shopRepository.findAll();
            return shops.stream()
                    .map(ShopDto::fromEntity)
                    .collect(Collectors.toList());
        } else if (userEntity.getRole().equals(UserRole.ROLE_BUSINESS)) {
            // User can only view their own shops
            List<ShopEntity> shops = shopRepository.findByOwner(userEntity);
            if (shops.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No shops found");
            }
            return shops.stream()
                    .map(ShopDto::fromEntity)
                    .collect(Collectors.toList());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You don't have permission to view shop list");
        }
    }
    //Read One
    // Get Shop by ID for Admin and Owner
    public ShopDto getShopById(Long shopId) {
        // Get the current user from AuthenticationFacade
        UserEntity userEntity = facade.getCurrentUserEntity();

        // Find the shop by ID
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop does not exist"));

        // Check user role and authorization
        if (userEntity.getRole().equals(UserRole.ROLE_ADMIN)) {
            // Admin can view any shop
            return ShopDto.fromEntity(shop);
        } else if (userEntity.getRole().equals(UserRole.ROLE_BUSINESS)) {
            // Business user can only view their own shop
            if (shop.getOwner().equals(userEntity)) {
                return ShopDto.fromEntity(shop);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "You are not authorized to view this store");
            }
        } else {
            // Unauthorized access
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You are not authorized to view this store");
        }
    }
    // Update shop info method
    public UserDto updateShopInfo(Long shopId, ShopEssentialDto dto) {
        // Get the current user from AuthenticationFacade
        UserEntity userEntity = facade.getCurrentUserEntity();

        // Check if the user has the ROLE_BUSINESS
        if (!userEntity.getRole().equals(UserRole.ROLE_BUSINESS)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You do not have permission to update shop information");
        }
        // Find the shop by ID
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop does not exist"));

        // Check if the user owns the shop
        if (!shop.getOwner().equals(userEntity)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You do not have permission to update this shop");
        }
        // Update shop info based on the provided EssentialInfoDto
        shop.setName(dto.getName());
        shop.setDescription(dto.getDescription());
        shop.setCategory(dto.getCategory());

        // Save updated shop info
        shopRepository.save(shop);
        return UserDto.fromEntity(userEntity);
    }

    // After update shop info, owner can create a Shop Register Request
    // Only shop have all essential info can request to upgrade ShopStatus :PREPARING to OPEN
    public ShopRegResponseDto shopRegister(Long shopId) {

        UserEntity userEntity = facade.getCurrentUserEntity();

        if (!userEntity.getRole().equals(UserRole.ROLE_BUSINESS))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "only Business user can access");

        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop does not exist"));

        if (!shop.getOwner().equals(userEntity)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to register this shop");
        }

        if (!shop.getStatus().equals(ShopStatus.PREPARING))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shop must be in PREPARING state to register");

        boolean existingShopReg = shopRegRepository.existsByShopIdAndStatus(shopId, ShopRegStatus.PENDING);
        if (existingShopReg) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This shop has already been registered for opening");
        }
        ShopRegistration shopReg = ShopRegistration.builder()
                .name(shop.getName())
                .description(shop.getDescription())
                .category(shop.getCategory())
                .owner(userEntity)
                .status(ShopRegStatus.PENDING)
                .owner(shop.getOwner())
                .businessNum(shop.getBusinessNum())
                .build();
        shopRegRepository.save(shopReg);
        return ShopRegResponseDto.fromEntity(shopReg);
    }

    // Admin and Owner can view Shop Registrations

    // Method to read all shop registrations based on user role
    public List<ShopRegResponseDto> readAllShopRegistrations() {
        UserEntity currentUser = facade.getCurrentUserEntity();
        // Admin
        if (currentUser.getRole().equals(UserRole.ROLE_ADMIN)) {
            return shopRegRepository.findAll().stream()
                    .map(ShopRegResponseDto::fromEntity)
                    .toList();
        } else if (currentUser.getRole().equals(UserRole.ROLE_BUSINESS)) {
            return shopRegRepository.findAllByOwnerId(currentUser.getId())
                    .stream()
                    .map(ShopRegResponseDto::fromEntity)
                    .toList();
        } else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // Method to read one shop registration by ID
    public ShopRegResponseDto readOneShopRegistration(Long shopRegId) {
        UserEntity currentUser = facade.getCurrentUserEntity();

        // Retrieve the shop registration by its ID
        ShopRegistration shopReg = shopRegRepository.findById(shopRegId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop registration not found"));

        // Only allow admin or the owner of the shop registration to view the details
        if (!shopReg.getOwner().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(UserRole.ROLE_ADMIN)) {
            log.warn("Unauthorized access attempt to shop registration ID: {} by user ID: {}", shopRegId, currentUser.getId());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to view this shop registration");
        }

        log.info("User ID: {} is retrieving shop registration ID: {}", currentUser.getId(), shopRegId);
        return ShopRegResponseDto.fromEntity(shopReg);
    }

    //Admin approval or decline shop registration

    // Method to accept a shop registration (Admin only)
    public ShopDto acceptShopReg(Long shopRegId) {
        UserEntity currentUser = facade.getCurrentUserEntity();
        // only admin can access
        if (!currentUser.getRole().equals(UserRole.ROLE_ADMIN))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Optional<ShopRegistration> optionalReg = shopRegRepository.findById(shopRegId);

        // if this registration does not exist
        if (optionalReg.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        ShopRegistration shopReg = optionalReg.get();
        // if ShopRegStatus is ACCEPTED or DECLINE
        if (!shopReg.getStatus().equals(ShopRegStatus.PENDING)) {
            log.info("Shop already registered");
            throw new ResponseStatusException(HttpStatus.OK, "The shop already registered");
        }
        shopReg.setStatus(ShopRegStatus.ACCEPTED);
        ShopEntity shop = shopRepository.findByBusinessNum(shopReg.getBusinessNum());

        shop.setStatus(ShopStatus.OPEN);

        shopRegRepository.save(shopReg);

        log.info("Shop registration ID: {} accepted by admin ID: {}", shopRegId, currentUser.getId());
        return ShopDto.fromEntity(shopRepository.save(shop));
    }

    public ShopRegResponseDto declineShopReg(Long shopRegId, ShopRegDeclineDto dto) {
        UserEntity currentUser = facade.getCurrentUserEntity();
        if (!currentUser.getRole().equals(UserRole.ROLE_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only admin can perform this action.");
        }

        Optional<ShopRegistration> optionalReg = shopRegRepository.findById(shopRegId);
        if (optionalReg.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop registration not found.");
        }

        ShopRegistration shopReg = optionalReg.get();
        if (!shopReg.getStatus().equals(ShopRegStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shop registration is not in PENDING status.");
        }

        shopReg.setStatus(ShopRegStatus.DECLINED);
        shopReg.setDeclineReason(dto.getReason());
        return ShopRegResponseDto.fromEntity(shopRegRepository.save(shopReg));
    }

    @Transactional
    public CloseResponseDto shopCloseRequest(Long shopId, CloseRequestDto dto) {
        UserEntity currentUser = facade.getCurrentUserEntity();
        log.info("Current user: {}", currentUser.getId());

        // Only ROLE_BUSINESS can request
        if (!currentUser.getRole().equals(UserRole.ROLE_BUSINESS)) {
            log.warn("Unauthorized access attempt by user ID: {}", currentUser.getId());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only business users can request to close a shop.");
        }

        // Find shop by Id
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found for the provided ID."));
        log.info("Shop ID: {}", shopId);

        // Ensure the shop belongs to the current user
        if (!shop.getOwner().getId().equals(currentUser.getId())) {
            log.warn("Shop ID: {} does not belong to the current user ID: {}", shopId, currentUser.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to close this shop.");
        }

        // Check if the store has any existing closure requests
        if (shopCloseRepository.existsByShopId(shop.getId())) {
            log.warn("A close request already exists for shop ID: {}", shop.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A close request already exists for this shop.");
        }

        // Only open stores can close
        log.info("Shop status: {}", shop.getStatus());
        if (!shop.getStatus().equals(ShopStatus.OPEN)) {
            log.warn("Cannot close shop with ID: {} as it is not open", shop.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only open shops can be closed.");
        }

        // Create request
        ShopCloseRequest closeRequest = ShopCloseRequest.builder()
                .shop(shop) // Set the shop entity here
                .owner(currentUser)
                .reason(dto.getReason())
                .build();

        log.info("Close request submitted for shop ID: {}", shop.getId());
        return CloseResponseDto.fromEntity(shopCloseRepository.save(closeRequest));
    }


    // Read All Close Request base on UserRole
    public List<CloseResponseDto> readAllCloseRequest() {
        UserEntity currentUser = facade.getCurrentUserEntity();

        // Get role of current user
        UserRole role = currentUser.getRole();

        List<ShopCloseRequest> closeRequests;

        if (role.equals(UserRole.ROLE_ADMIN)) {
            // Admin can read all
            log.info("Admin user ID: {} is retrieving all close requests", currentUser.getId());
            closeRequests = shopCloseRepository.findAll();
        } else if (role.equals(UserRole.ROLE_BUSINESS)) {
            // ROLE_BUSINESS can only view by userId
            log.info("Business user ID: {} is retrieving close requests for their shop", currentUser.getId());
            // Find shop by owner id
            ShopEntity shop = shopRepository.findByOwnerId(currentUser.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found for the current user."));

            closeRequests = shopCloseRepository.findAllByShopId(shop.getId());
        } else {
            log.warn("Unauthorized access attempt by user ID: {}", currentUser.getId());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to view close requests.");
        }

        if (closeRequests.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No close requests found.");
        }

        return closeRequests.stream()
                .map(CloseResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // Read One Close Request
    public CloseResponseDto readOneCloseRequest(Long closeRequestId) {
        UserEntity currentUser = facade.getCurrentUserEntity();

        // Get closeRequest
        ShopCloseRequest closeRequest = shopCloseRepository.findById(closeRequestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Close request not found"));

        // Case User is admin
        if (currentUser.getRole().equals(UserRole.ROLE_ADMIN)) {
            log.info("Admin user ID: {} is retrieving close request ID: {}", currentUser.getId(), closeRequestId);
            return CloseResponseDto.fromEntity(closeRequest);
        }

        // case user is ROLE_BUSINESS
        if (currentUser.getRole().equals(UserRole.ROLE_BUSINESS)) {
            //
            ShopEntity shop = shopRepository.findByOwnerId(currentUser.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found for the current user."));

            //
            if (!closeRequest.getShop().getId().equals(shop.getId())) {
                log.warn("Unauthorized access attempt by business user ID: {} to close request ID: {}", currentUser.getId(), closeRequestId);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to view this close request.");
            }

            log.info("Business user ID: {} is retrieving close request ID: {}", currentUser.getId(), closeRequestId);
            return CloseResponseDto.fromEntity(closeRequest);
        }

        //
        log.warn("Unauthorized access attempt by user ID: {} to close request ID: {}", currentUser.getId(), closeRequestId);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to view this close request.");
    }
    // Approval Close request
    @Transactional
    public ShopDto closeShop(Long closeReqId) {
        UserEntity currentUser = facade.getCurrentUserEntity();

        log.info("current user id: {} username: {} ",currentUser.getId(), currentUser.getUsername());

        if (!currentUser.getRole().equals(UserRole.ROLE_ADMIN)) {
            log.warn("Unauthorized access attempt by user ID: {}", currentUser.getId());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only admins can process close requests");
        }

        Optional<ShopCloseRequest> closeReqOpt = shopCloseRepository.findById(closeReqId);

        if (closeReqOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        ShopCloseRequest request = closeReqOpt.get();
        ShopEntity shop = request.getShop();
        shop.setStatus(ShopStatus.CLOSED);
       log.info("Shop with ID: {} has been closed based on close request ID: {}", shop.getId(), closeReqId);

       // delete close request
        shopCloseRepository.deleteById(closeReqId);
        return ShopDto.fromEntity(shopRepository.save(shop));
    }
    // Process the close request and update shop status
    private void processCloseRequest(ShopCloseRequest request, ShopEntity shop) {
        shopCloseRepository.delete(request);
        shop.setStatus(ShopStatus.CLOSED);
    }

    // Search Shop by nameKeyword and category
    public List<ShopDto> searchShops(String nameKeyword, String category) {
        UserEntity currentUser = facade.getCurrentUserEntity();
        checkUserActive(currentUser);

        List<ShopEntity> shopList = performShopSearch(nameKeyword, category);

        log.info("Search completed with name query: {} and category: {}", nameKeyword, category);
        return shopList.stream().map(ShopDto::fromEntity).toList();
    }

    // Check if the current user is active
    private void checkUserActive(UserEntity user) {
        if (user.getRole().equals(UserRole.ROLE_INACTIVE)) {
            log.warn("Unauthorized access attempt by inactive user ID: {}", user.getId());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inactive users cannot search for shops");
        }
    }

    // View shop search based on criteria (keyword, category)
    private List<ShopEntity> performShopSearch(String nameKeyword, String category) {
        if (nameKeyword == null && category == null) {
            return shopRepository.findAllByOrderByLastPurchasedDesc();
        } else if (nameKeyword != null && category == null) {
            return shopRepository.findAllByNameContaining(nameKeyword);
        } else if (nameKeyword == null && category != null) {
            return findShopsByCategory(category);
        } else {
            return findShopsByNameAndCategory(nameKeyword, category);
        }
    }

    // Find shops by category
    private List<ShopEntity> findShopsByCategory(String category) {
        try {
            return shopRepository.findAllByCategory(ShopCategory.valueOf(category));
        } catch (IllegalArgumentException e) {
            log.error("Invalid category provided: {}", category);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category");
        }
    }

    // Find shops by name and category
    private List<ShopEntity> findShopsByNameAndCategory(String nameKeyword, String category) {
        try {
            return shopRepository.findAllByNameContainingAndCategory(nameKeyword, ShopCategory.valueOf(category));
        } catch (IllegalArgumentException e) {
            log.error("Invalid category provided: {}", category);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category");
        }
    }
}
