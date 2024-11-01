package book.store.controller;

import book.store.dto.cart.item.CartItemRequestDto;
import book.store.dto.cart.item.CartItemUpdateQuantityDto;
import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.service.shopping.cart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping Cart Controller Management",
        description = "Endpoints for managing items in Shopping Carts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Add new item to a shopping cart",
            description = "Adds a new item to the user's shopping cart, "
                    + "with quantity specified in the request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Item successfully added to cart"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid item or quantity specified"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied")
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ShoppingCartResponseDto addCartItem(
            @RequestBody @Valid CartItemRequestDto cartItemRequestDto) {
        return shoppingCartService.addCartItem(cartItemRequestDto);
    }

    @Operation(summary = "Get shopping cart",
            description = "Retrieves the current shopping cart of the authenticated user,"
                    + " including all cart items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved shopping cart"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied")
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ShoppingCartResponseDto getShoppingCart() {
        return shoppingCartService.get();
    }

    @Operation(summary = "Update a cart item by ID",
            description = "Updates the quantity of a specific item in the user's shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Item quantity successfully updated"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid quantity specified"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied"),
            @ApiResponse(responseCode = "404",
                    description = "Item not found in the cart")
    })
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/items/{id}")
    public ShoppingCartResponseDto updateCartItemBy(
            @RequestBody @Valid CartItemUpdateQuantityDto updateQuantityDto,
            @PathVariable Long id) {
        return shoppingCartService.update(updateQuantityDto, id);
    }

    @Operation(summary = "Delete a cart item by ID",
            description = "Removes a specific item from the user's shopping cart by item ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Item successfully removed from cart"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized. Authentication required"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden. Permission denied"),
            @ApiResponse(responseCode = "404",
                    description = "Item not found in the cart")
    })
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/items/{id}")
    public void deleteCartItemById(@PathVariable Long id) {
        shoppingCartService.delete(id);
    }
}
