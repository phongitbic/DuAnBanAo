package Utils;

import Model.ProductDetails;
import Service.QuanLySanPhamService;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.BorderLayout;

import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.swing.table.DefaultTableModel;

public class QRCodeScanner {

    private boolean isScanning = false;
    private Thread qrCodeScannerThread;
    private Webcam webcam;
    private JLabel pnWebCam;
    private Service.QuanLySanPhamService qlsp;
    private JTable tblSPCT;

    public QRCodeScanner(JLabel pnWebCam, QuanLySanPhamService qlsp, JTable tblSPCT) {
        this.pnWebCam = pnWebCam;
        this.qlsp = qlsp;
        this.tblSPCT = tblSPCT;
    }

    public void startWebcam(JTable table) {
        webcam = Webcam.getDefault();
        if (webcam == null) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy webcam.");
            return;
        }

        if (webcam.isOpen()) {
            webcam.close();
        }
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);

        pnWebCam.removeAll();
        pnWebCam.setLayout(new BorderLayout());
        pnWebCam.add(panel, BorderLayout.CENTER);
        pnWebCam.revalidate();
        pnWebCam.repaint();

        webcam.open();

        qrCodeScannerThread = new Thread(() -> {
            while (isScanning) {
                BufferedImage image = webcam.getImage();
                if (image == null) {
                    continue;
                }

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                try {
                    Result result = new MultiFormatReader().decode(bitmap);
                    String productDetailCode = result.getText();

                    // Xử lý kết quả quét mã QR
                    DefaultTableModel dtm = (DefaultTableModel) table.getModel();
                    dtm.setRowCount(0);
                    int stt = 1;
                    if (!qlsp.getListQR(productDetailCode).isEmpty()) {
                        for (ProductDetails proDe : qlsp.getListQR(productDetailCode)) {
                            Object[] rowData = {
                                stt++,
                                proDe.getProductDetail_code(),
                                proDe.getProduct_code(),
                                proDe.getProduct_name(),
                                proDe.getCategory_name(),
                                proDe.getColor(),
                                proDe.getSize(),
                                proDe.getImage(),
                                proDe.getStyle(),
                                proDe.getQuantity(),
                                proDe.getPrice()
                            };
                            dtm.addRow(rowData);
                        }
                    } else {
                        dtm.addRow(new Object[]{"", "", "", "", "Danh sách trống", "", "", "", "", "", ""});
                    }
//                    qlsp.loadTableDetail(tblSPCT, qlsp.getListQR(productDetailCode));

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
            if (webcam != null) {
                webcam.close();
            }
            if (qrCodeScannerThread != null && qrCodeScannerThread.isAlive()) {
                qrCodeScannerThread.interrupt();
            }
            JOptionPane.showMessageDialog(null, "Đã dừng quét mã QR.");
        }
    }

    public void toggleScanning() {
        if (isScanning) {
            stopWebcam();
        } else {
            isScanning = true;
            startWebcam(tblSPCT);
        }
    }
}
