package com.qr.code.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

@Controller
@RequestMapping("/api/qrcode")
public class QRCodeController {

	@GetMapping(value = "/generate/{text}", produces = MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody byte[] generateQRCode(@PathVariable String text) throws Exception {
		int width = 300;
		int height = 300;

		BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

		return outputStream.toByteArray();
	}

	@PostMapping("/decode")
    public ResponseEntity<String> decodeQRCode(@RequestParam("image") InputStream imageStream) {
            try {
                BufferedImage bufferedImage = ImageIO.read(imageStream);

                if (bufferedImage == null) {
                    return ResponseEntity.badRequest().body("Invalid image format");
                }

                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
                Result result = new MultiFormatReader().decode(binaryBitmap);

                return ResponseEntity.ok(result.getText());
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Error decoding QR code");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid QR code");
            }
        }
}
