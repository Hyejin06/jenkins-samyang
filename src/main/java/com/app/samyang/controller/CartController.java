package com.app.samyang.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.samyang.entity.Cart;
import com.app.samyang.entity.Member;
import com.app.samyang.entity.Product;
import com.app.samyang.service.CartService;
import com.app.samyang.service.ProductService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;
	private final ProductService productService;

	@GetMapping("/cart")
	public String viewCart(Model model, HttpSession session) {
		Member loginMember = (Member) session.getAttribute("loginMember");
		if (loginMember == null)
			return "redirect:/member/login";

		String memberId = loginMember.getId();

		List<Cart> cartList = cartService.getCartList(memberId);
		model.addAttribute("cartList", cartList);

		int totalPrice = cartList.stream().mapToInt(c -> c.getPrice() * c.getQuantity()).sum();
		model.addAttribute("totalPrice", totalPrice);
		return "cart/cart";
	}

	@PostMapping("/cart/add")
	public String addToCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity,
			HttpSession session) {

		Member loginMember = (Member) session.getAttribute("loginMember");
		if (loginMember == null)
			return "redirect:/member/login";

		String memberId = loginMember.getId();
		Product product = productService.findById(productId);

		Cart cart = Cart.builder().memberId(memberId).productId(product.getNo()).pname(product.getPname())
				.price(product.getPrice()).quantity(quantity).img1(product.getImg1()).build();

		cartService.addToCart(cart);
		return "redirect:/cart";
	}

	@PostMapping("/cart/delete")
	public String deleteCartItem(@RequestParam("id") Long id, HttpSession session) {
		cartService.removeFromCart(id);
		return "redirect:/cart";
	}

	@PostMapping("/order")
	public String orderSelected(@RequestParam(required = false, name = "selectedItems") List<Long> selectedItems,
			HttpSession session, Model model) {

		Member loginMember = (Member) session.getAttribute("loginMember");
		if (loginMember == null)
			return "redirect:/member/login";

		if (selectedItems == null || selectedItems.isEmpty())
			return "redirect:/cart";

		List<Cart> orderList = cartService.findByIds(selectedItems);

		int totalPrice = orderList.stream().mapToInt(item -> item.getPrice() * item.getQuantity()).sum();
		int delivery = totalPrice > 0 ? 3000 : 0;
		int finalPrice = totalPrice + delivery;

		model.addAttribute("cartItems", orderList);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("delivery", delivery);
		model.addAttribute("finalPrice", finalPrice);

		return "cart/order";
	}

	@GetMapping("/cart/success")
	public String success(@RequestParam Map<String, String> params, HttpSession session, Model model) {
		Member loginMember = (Member) session.getAttribute("loginMember");
		if (loginMember != null) {
			cartService.clearCart(loginMember.getId());
		}
		model.addAttribute("result", params);
		return "cart/success";
	}

	@GetMapping("/cart/fail")
	public String fail(@RequestParam Map<String, String> params, Model model) {
		model.addAttribute("error", params);
		return "cart/fail";
	}

	@PostMapping("/cart/updateQuantity")
	@ResponseBody
	public String updateQuantity(@RequestParam("id") Long id, @RequestParam("quantity") int quantity) {
		Cart cart = cartService.findById(id);
		if (cart != null) {
			cart.setQuantity(quantity);
			cartService.addToCart(cart);
		}
		return "ok";
	}

	@PostMapping("/cart/checkout")
	public String checkout(@RequestParam Map<String, String> orderInfo, HttpSession session)
			throws UnsupportedEncodingException {
		Member member = (Member) session.getAttribute("loginMember");
		if (member == null)
			return "redirect:/member/login";

		// ✅ 결제 성공 시 장바구니 비우기
		cartService.clearCart(member.getId());

		// ✅ 리다이렉트 URL로 모든 파라미터 넣기
		String redirectUrl = "/cart/success" + "?orderId=" + orderInfo.get("orderId") + "&postcode="
				+ URLEncoder.encode(orderInfo.get("postcode"), "UTF-8") + "&address="
				+ URLEncoder.encode(orderInfo.get("address"), "UTF-8") + "&detailAddress="
				+ URLEncoder.encode(orderInfo.get("detailAddress"), "UTF-8") + "&finalPrice="
				+ orderInfo.get("finalPrice"); // ✅ 여기!

		return "redirect:" + redirectUrl;
	}
}
