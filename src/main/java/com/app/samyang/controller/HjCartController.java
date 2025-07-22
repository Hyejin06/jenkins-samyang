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

import com.app.samyang.entity.HjCart;
import com.app.samyang.entity.HjMember;
import com.app.samyang.entity.HjProduct;
import com.app.samyang.service.HjCartService;
import com.app.samyang.service.HjProductService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HjCartController {

	private final HjCartService hjCartService;
	private final HjProductService hjProductService;

	@GetMapping("/hjcart")
	public String viewCart(Model model, HttpSession session) {
		HjMember hjloginMember = (HjMember) session.getAttribute("hjloginMember");
		if (hjloginMember == null)
			return "redirect:/member/hjlogin";

		String memberId = hjloginMember.getId(); // ✅ 진짜 로그인 ID

		List<HjCart> cartList = hjCartService.getCartList(memberId);
		model.addAttribute("cartList", cartList);

		int totalPrice = cartList.stream().mapToInt(c -> c.getPrice() * c.getQuantity()).sum();

		model.addAttribute("totalPrice", totalPrice);
		return "cart/hjcart";
	}

	@PostMapping("/cart/hjadd")
	public String addToCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity,
			HttpSession session) {

		HjMember hjloginMember = (HjMember) session.getAttribute("hjloginMember");
		if (hjloginMember == null)
			return "redirect:/member/hjlogin";

		String memberId = hjloginMember.getId();

		HjProduct hjProduct = hjProductService.findById(productId);

		HjCart hjCart = HjCart.builder().memberId(memberId).productId(hjProduct.getNo()).pname(hjProduct.getPname())
				.price(hjProduct.getPrice()).quantity(quantity).img1(hjProduct.getImg1()).build();

		hjCartService.addToCart(hjCart);
		return "redirect:/hjcart";
	}

	@PostMapping("/cart/hjdelete")
	public String deleteCartItem(@RequestParam("id") Long id, HttpSession session) {
		hjCartService.removeFromCart(id);
		return "redirect:/hjcart";
	}

	@PostMapping("/hjorder")
	public String orderSelected(@RequestParam(required = false, name = "selectedItems") List<Long> selectedItems,
			HttpSession session, Model model) {

		HjMember hjloginMember = (HjMember) session.getAttribute("hjloginMember");
		if (hjloginMember == null)
			return "redirect:/member/hjlogin";

		List<HjCart> orderList;
		if (selectedItems != null && !selectedItems.isEmpty()) {
			orderList = hjCartService.findByIds(selectedItems);
		} else {
			return "redirect:/hjcart";
		}

		int totalPrice = orderList.stream().mapToInt(item -> item.getPrice() * item.getQuantity()).sum();
		int delivery = totalPrice > 0 ? 4000 : 0;
		int finalPrice = totalPrice + delivery;

		model.addAttribute("cartItems", orderList);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("delivery", delivery);
		model.addAttribute("finalPrice", finalPrice);

		return "cart/hjorder";
	}

	@GetMapping("/cart/hjsuccess")
	public String success(@RequestParam Map<String, String> params, HttpSession session, Model model) {
		HjMember hjloginMember = (HjMember) session.getAttribute("hjloginMember");
		if (hjloginMember != null) {
			// ✅ 결제 성공 시 장바구니 비우기
			hjCartService.clearCart(hjloginMember.getId());
		}

		model.addAttribute("result", params);
		return "cart/hjsuccess";
	}

	@GetMapping("/cart/hjfail")
	public String fail(@RequestParam Map<String, String> params, Model model) {
		model.addAttribute("error", params);
		return "cart/hjfail";
	}

	@PostMapping("/cart/hjupdateQuantity")
	@ResponseBody
	public String updateQuantity(@RequestParam("id") Long id, @RequestParam("quantity") int quantity) {
		HjCart cart = hjCartService.findById(id);
		if (cart != null) {
			cart.setQuantity(quantity);
			hjCartService.addToCart(cart); // save 처리
		}
		return "ok";
	}

	@PostMapping("/cart/hjcheckout")
	public String checkout(@RequestParam Map<String, String> orderInfo, HttpSession session)
			throws UnsupportedEncodingException {
		HjMember member = (HjMember) session.getAttribute("hjloginMember");
		if (member == null)
			return "redirect:/member/hjlogin";

		// ✅ 결제 성공 시 장바구니 비우기
		hjCartService.clearCart(member.getId());

		// ✅ 리다이렉트 URL로 모든 파라미터 넣기
		String redirectUrl = "/cart/hjsuccess" + "?orderId=" + orderInfo.get("orderId") + "&postcode="
				+ URLEncoder.encode(orderInfo.get("postcode"), "UTF-8") + "&address="
				+ URLEncoder.encode(orderInfo.get("address"), "UTF-8") + "&detailAddress="
				+ URLEncoder.encode(orderInfo.get("detailAddress"), "UTF-8") + "&finalPrice="
				+ orderInfo.get("finalPrice"); // ✅ 여기!

		return "redirect:" + redirectUrl;
	}

}
