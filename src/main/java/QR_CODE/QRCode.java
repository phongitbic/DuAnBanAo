////package QR_CODE;
////
////import Service.QuanLyBanHang;
////import com.github.sarxos.webcam.Webcam;
////import com.github.sarxos.webcam.WebcamPanel;
////import com.github.sarxos.webcam.WebcamResolution;
////import com.google.zxing.*;
////import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
////import com.google.zxing.common.HybridBinarizer;
////import java.awt.BorderLayout;
////import java.awt.GridBagConstraints;
////import java.awt.GridBagLayout;
////
////import javax.swing.*;
////import java.awt.image.BufferedImage;
////
////public class QRCode {
////
////    private boolean isScanning = false;
////    private Thread qrCodeScannerThread;
////    private Webcam cam;
////    private JPanel camera;
////    private Service.QuanLyBanHang ql;
////    private JTable tblDSSanPham;
////    private JLabel anhQRcode;
////
////    public QRCode(JPanel camera, JLabel anhQRcode, QuanLyBanHang ql, JTable tblDSSanPham) {
////    this.camera = camera;
////    this.anhQRcode = anhQRcode;
////    this.ql = ql;
////    this.tblDSSanPham = tblDSSanPham;
////}
////
////    public void startWebcam() {
////    cam = Webcam.getDefault();
////    if (cam == null) {
////        JOptionPane.showMessageDialog(null, "Không tìm thấy webcam.");
////        return;
////    }
////
////    if (cam.isOpen()) {
////        cam.close();
////    }
////    cam.setViewSize(WebcamResolution.VGA.getSize());
////
////    WebcamPanel panel = new WebcamPanel(cam);
////    panel.setPreferredSize(WebcamResolution.VGA.getSize());
////    panel.setFPSDisplayed(true);
////    panel.setDisplayDebugInfo(true);
////    panel.setImageSizeDisplayed(true);
////    panel.setMirrored(true);
////
////    camera.removeAll();
////    camera.setLayout(new BorderLayout());
////    camera.add(panel, BorderLayout.CENTER);
////    camera.revalidate();
////    camera.repaint();
////
////    cam.open();
////
////    qrCodeScannerThread = new Thread(() -> {
////        while (isScanning) {
////            BufferedImage image = cam.getImage();
////            if (image == null) {
////                continue;
////            }
////
////            LuminanceSource source = new BufferedImageLuminanceSource(image);
////            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
////            try {
////                Result result = new MultiFormatReader().decode(bitmap);
////                String productDetailCode = result.getText();
////
////                // Xử lý kết quả quét mã QR
////                ql.loadDanhSachSPCT(tblDSSanPham, ql.getListQRCode(productDetailCode));
////
////            } catch (NotFoundException e) {
////                // Không tìm thấy mã QR trong khung hiện tại, tiếp tục quét
////            }
////        }
////    });
////    qrCodeScannerThread.start();
////}
////
////    
////
////   public void stopWebcam() {
////    if (isScanning) {
////        isScanning = false;
////        if (cam != null) {
////            cam.close();
////        }
////        if (qrCodeScannerThread != null && qrCodeScannerThread.isAlive()) {
////            qrCodeScannerThread.interrupt();
////        }
////
////        camera.removeAll();
////        
////        // Sử dụng GridBagLayout để căn giữa anhQRcode
////        camera.setLayout(new GridBagLayout());
////        GridBagConstraints gbc = new GridBagConstraints();
////        gbc.gridx = 0;
////        gbc.gridy = 0;
////        gbc.anchor = GridBagConstraints.CENTER;
////        camera.add(anhQRcode, gbc);
////        
////        camera.revalidate();
////        camera.repaint();
////
////        JOptionPane.showMessageDialog(null, "Đã dừng quét mã QR.");
////    }
////}
////
////
////
////    public void toggleScanning() {
////        if (isScanning) {
////            stopWebcam();
////        } else {
////            isScanning = true;
////            startWebcam();
////        }
////    }
////}
//
//
//package QR_CODE;
//
//import Service.QuanLyBanHang;
//import com.github.sarxos.webcam.Webcam;
//import com.github.sarxos.webcam.WebcamPanel;
//import com.github.sarxos.webcam.WebcamResolution;
//import com.google.zxing.*;
//import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
//import com.google.zxing.common.HybridBinarizer;
//import java.awt.BorderLayout;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.image.BufferedImage;
//import javax.swing.*;
//
//public class QRCode {
//
//    private boolean isScanning = false;
//    private Thread qrCodeScannerThread;
//    private Webcam cam;
//    private JPanel camera;
//    private QuanLyBanHang ql;
//    private JTable tblDSSanPham;
//    private JLabel anhQRcode;
//    private OnScanResultListener scanResultListener;
//
//    public QRCode(JPanel camera, JLabel anhQRcode, QuanLyBanHang ql, JTable tblDSSanPham) {
//        this.camera = camera;
//        this.anhQRcode = anhQRcode;
//        this.ql = ql;
//        this.tblDSSanPham = tblDSSanPham;
//    }
//
//    public void setOnScanResultListener(OnScanResultListener listener) {
//        this.scanResultListener = listener;
//    }
//
//    public void startWebcam() {
//        cam = Webcam.getDefault();
//        if (cam == null) {
//            JOptionPane.showMessageDialog(null, "Không tìm thấy webcam.");
//            return;
//        }
//
//        if (cam.isOpen()) {
//            cam.close();
//        }
//        cam.setViewSize(WebcamResolution.VGA.getSize());
//
//        WebcamPanel panel = new WebcamPanel(cam);
//        panel.setPreferredSize(WebcamResolution.VGA.getSize());
//        panel.setFPSDisplayed(true);
//        panel.setDisplayDebugInfo(true);
//        panel.setImageSizeDisplayed(true);
//        panel.setMirrored(true);
//
//        camera.removeAll();
//        camera.setLayout(new BorderLayout());
//        camera.add(panel, BorderLayout.CENTER);
//        camera.revalidate();
//        camera.repaint();
//
//        cam.open();
//
//        qrCodeScannerThread = new Thread(() -> {
//            while (isScanning) {
//                BufferedImage image = cam.getImage();
//                if (image == null) {
//                    continue;
//                }
//
//                LuminanceSource source = new BufferedImageLuminanceSource(image);
//                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//                try {
//                    Result result = new MultiFormatReader().decode(bitmap);
//                    String productDetailCode = result.getText();
//
//                    // Xử lý kết quả quét mã QR
//                    ql.loadDanhSachSPCT(tblDSSanPham, ql.getListQRCode(productDetailCode));
//
//                    // Gọi callback khi quét thành công
//                    if (scanResultListener != null) {
//                        scanResultListener.onScanResult(productDetailCode);
//                    }
//
//                } catch (NotFoundException e) {
//                    // Không tìm thấy mã QR trong khung hiện tại, tiếp tục quét
//                }
//            }
//        });
//        qrCodeScannerThread.start();
//    }
//
//    public void stopWebcam() {
//        if (isScanning) {
//            isScanning = false;
//            if (cam != null) {
//                cam.close();
//            }
//            if (qrCodeScannerThread != null && qrCodeScannerThread.isAlive()) {
//                qrCodeScannerThread.interrupt();
//            }
//
//            camera.removeAll();
//
//            // Sử dụng GridBagLayout để căn giữa anhQRcode
//            camera.setLayout(new GridBagLayout());
//            GridBagConstraints gbc = new GridBagConstraints();
//            gbc.gridx = 0;
//            gbc.gridy = 0;
//            gbc.anchor = GridBagConstraints.CENTER;
//            camera.add(anhQRcode, gbc);
//
//            camera.revalidate();
//            camera.repaint();
//
//            JOptionPane.showMessageDialog(null, "Đã dừng quét mã QR.");
//        }
//    }
//
//    public void toggleScanning() {
//        if (isScanning) {
//            stopWebcam();
//        } else {
//            isScanning = true;
//            startWebcam();
//        }
//    }
//
//    public interface OnScanResultListener {
//        void onScanResult(String productDetailCode);
//    }
//}
//


package QR_CODE;

import Service.QuanLyBanHang;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import javax.swing.*;

public class QRCode {

    private boolean isScanning = false;
    private Thread qrCodeScannerThread;
    private Webcam cam;
    private JPanel camera;
    private QuanLyBanHang ql;
    private JTable tblDSSanPham;
    private JLabel anhQRcode;
    private OnScanResultListener scanResultListener;
    private HashSet<String> scannedCodes; // Tập hợp lưu mã QR đã quét

    public QRCode(JPanel camera, JLabel anhQRcode, QuanLyBanHang ql, JTable tblDSSanPham) {
        this.camera = camera;
        this.anhQRcode = anhQRcode;
        this.ql = ql;
        this.tblDSSanPham = tblDSSanPham;
        this.scannedCodes = new HashSet<>();
    }

    public void setOnScanResultListener(OnScanResultListener listener) {
        this.scanResultListener = listener;
    }

    public void startWebcam() {
        cam = Webcam.getDefault();
        if (cam == null) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy webcam.");
            return;
        }

        if (cam.isOpen()) {
            cam.close();
        }
        cam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel panel = new WebcamPanel(cam);
        panel.setPreferredSize(WebcamResolution.VGA.getSize());
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);

        camera.removeAll();
        camera.setLayout(new BorderLayout());
        camera.add(panel, BorderLayout.CENTER);
        camera.revalidate();
        camera.repaint();

        cam.open();

        qrCodeScannerThread = new Thread(() -> {
            while (isScanning) {
                BufferedImage image = cam.getImage();
                if (image == null) {
                    continue;
                }

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                try {
                    Result result = new MultiFormatReader().decode(bitmap);
                    String productDetailCode = result.getText();

                    if (!scannedCodes.contains(productDetailCode)) {
                        scannedCodes.add(productDetailCode);

                        // Xử lý kết quả quét mã QR
                        ql.loadDanhSachSPCT(tblDSSanPham, ql.getListQRCode(productDetailCode));

                        // Gọi callback khi quét thành công
                        if (scanResultListener != null) {
                            scanResultListener.onScanResult(productDetailCode);
                        }
                    }

                } catch (NotFoundException e) {
                    // Không tìm thấy mã QR trong khung hiện tại, tiếp tục quét
                }
            }
        });
        qrCodeScannerThread.start();
    }

    public void stopWebcam() {
        if (isScanning) {
            isScanning = false;
            if (cam != null) {
                cam.close();
            }
            if (qrCodeScannerThread != null && qrCodeScannerThread.isAlive()) {
                qrCodeScannerThread.interrupt();
            }

            camera.removeAll();

            // Sử dụng GridBagLayout để căn giữa anhQRcode
            camera.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            camera.add(anhQRcode, gbc);

            camera.revalidate();
            camera.repaint();

            JOptionPane.showMessageDialog(null, "Đã dừng quét mã QR.");
        }
    }

    public void toggleScanning() {
        if (isScanning) {
            stopWebcam();
        } else {
            isScanning = true;
            startWebcam();
        }
    }

    public interface OnScanResultListener {
        void onScanResult(String productDetailCode);
    }
}

