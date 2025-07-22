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

import com.app.samyang.entity.JyCart;
import com.app.samyang.entity.JyMember;
import com.app.samyang.entity.JyProduct;
import com.app.samyang.service.JyCartService;
import com.app.samyang.service.JyProductService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class JyCartController {

	private final JyCartService jyCartService;
	private final JyProductService jyProductService;

	@GetMapping("/jycart")
	public String viewCart(Model model, HttpSession session) {
		JyMember jyloginMember = (JyMember) session.getAttribute("jyloginMember");
		if (jyloginMember == null)
			return "redirect:/member/jylogin";

		String memberId = jyloginMember.getId(); // ✅ 진짜 로그인 ID

		List<JyCart> cartList = jyCartService.getCartList(memberId);
		model.addAttribute("cartList", cartList);

		int totalPrice = cartList.stream().mapToInt(c -> c.getPrice() * c.getQuantity()).sum();

		model.addAttribute("totalPrice", totalPrice);
		return "cart/jycart";
	}

	@PostMapping("/cart/jyadd")
	public String addToCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity,
			HttpSession session) {

		JyMember jyloginMember = (JyMember) session.getAttribute("jyloginMember");
		if (jyloginMember == null)
			return "redirect:/member/jylogin";

		String memberId = jyloginMember.getId();

		JyProduct jyProduct = jyProductService.findById(productId);

		JyCart jyCart = JyCart.builder().memberId(memberId).productId(jyProduct.getNo()).pname(jyProduct.getPname())
				.price(jyProduct.getPrice()).quantity(quantity).img1(jyProduct.getImg1()).build();

		jyCartService.addToCart(jyCart);
		return "redirect:/jycart";
	}

	@PostMapping("/cart/jydelete")
	public String deleteCartItem(@RequestParam("id") Long id, HttpSession session) {
		jyCartService.removeFromCart(id);
		return "redirect:/jycart";
	}

	@PostMapping("/jyorder")
	public String orderSelected(@RequestParam(required = false, name = "selectedItems") List<Long> selectedItems,
			HttpSession session, Model model) {

		JyMember jyloginMember = (JyMember) session.getAttribute("jyloginMember");
		if (jyloginMember == null)
			return "redirect:/member/jylogin";

		List<JyCart> orderList;
		if (selectedItems != null && !selectedItems.isEmpty()) {
			orderList = jyCartService.findByIds(selectedItems);
		} else {
			return "redirect:/jycart";
		}

		int totalPrice = orderList.stream().mapToInt(item -> item.getPrice() * item.getQuantity()).sum();
		int delivery = totalPrice > 0 ? 3000 : 0;
		int finalPrice = totalPrice + delivery;

		model.addAttribute("cartItems", orderList);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("delivery", delivery);
		model.addAttribute("finalPrice", finalPrice);

		return "cart/jyorder";
	}

	@GetMapping("/cart/jysuccess")
	public String success(@RequestParam Map<String, String> params, HttpSession session, Model model) {
		JyMember loginMember = (JyMember) session.getAttribute("jyloginMember");
		if (loginMember != null) {
			// ✅ 결제 성공 시 장바구니 비우기
			jyCartService.clearCart(loginMember.getId());
		}

		model.addAttribute("result", params);
		return "cart/jysuccess";
	}

	@GetMapping("/cart/jyfail")
	public String fail(@RequestParam Map<String, String> params, Model model) {
		model.addAttribute("error", params);
		return "cart/jyfail";
	}

	@PostMapping("/cart/jyupdateQuantity")
	@ResponseBody
	public String updateQuantity(@RequestParam("id") Long id, @RequestParam("quantity") int quantity) {
		JyCart cart = jyCartService.findById(id);
		if (cart != null) {
			cart.setQuantity(quantity);
			jyCartService.addToCart(cart); // save 처리
		}
		return "ok";
	}

	@PostMapping("/cart/jycheckout")
	public String checkout(@RequestParam Map<String, String> orderInfo, HttpSession session)
			throws UnsupportedEncodingException {
		JyMember member = (JyMember) session.getAttribute("jyloginMember");
		if (member == null)
			return "redirect:/member/jylogin";

		// ✅ 결제 성공 시 장바구니 비우기
		jyCartService.clearCart(member.getId());

		// ✅ 리다이렉트 URL로 모든 파라미터 넣기
		String redirectUrl = "/cart/jysuccess" + "?orderId=" + orderInfo.get("orderId") + "&postcode="
				+ URLEncoder.encode(orderInfo.get("postcode"), "UTF-8") + "&address="
				+ URLEncoder.encode(orderInfo.get("address"), "UTF-8") + "&detailAddress="
				+ URLEncoder.encode(orderInfo.get("detailAddress"), "UTF-8") + "&finalPrice="
				+ orderInfo.get("finalPrice"); // ✅ 여기!

		return "redirect:" + redirectUrl;
	}

}
