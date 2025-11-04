package org.delcom.starter.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

@RestController
public class HomeController {

    private static final double AMBANG_BATAS_NILAI_SANGAT_MEMUASKAN = 79.5;
    private static final double AMBANG_BATAS_NILAI_HAMPIR_SANGAT_MEMUASKAN = 72.0;
    private static final double AMBANG_BATAS_NILAI_MEMUASKAN = 64.5;
    private static final double AMBANG_BATAS_NILAI_CUKUP_MEMUASKAN = 57.0;
    private static final double AMBANG_BATAS_NILAI_SEDANG = 49.5;
    private static final double AMBANG_BATAS_NILAI_PERLU_PERBAIKAN = 34.0;

    private static final Map<String, String> KAMUS_PROGRAM_STUDI_RAHASIA = buatKamusProgramStudi();

    private static Map<String, String> buatKamusProgramStudi() {
        Map<String, String> ensiklopediaProgram = new HashMap<>();
        ensiklopediaProgram.put("11S", "Sarjana Informatika");
        ensiklopediaProgram.put("12S", "Sarjana Sistem Informasi");
        ensiklopediaProgram.put("14S", "Sarjana Teknik Elektro");
        ensiklopediaProgram.put("21S", "Sarjana Manajemen Rekayasa");
        ensiklopediaProgram.put("22S", "Sarjana Teknik Metalurgi");
        ensiklopediaProgram.put("31S", "Sarjana Teknik Bioproses");
        ensiklopediaProgram.put("114", "Diploma 4 Teknologi Rekasaya Perangkat Lunak");
        ensiklopediaProgram.put("113", "Diploma 3 Teknologi Informasi");
        ensiklopediaProgram.put("133", "Diploma 3 Teknologi Komputer");
        return Map.copyOf(ensiklopediaProgram);
    }

    @GetMapping("/")
    public String hello() {
        return "Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping("/informasiNim/{nim}")
    public ResponseEntity<String> informasiNim(@PathVariable String nim) {
        try {
            String hasilAkhir = prosesDecodingNim(nim);
            return ResponseEntity.ok(hasilAkhir);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/perolehanNilai/{strBase64}")
    public ResponseEntity<String> perolehanNilai(@PathVariable String strBase64) {
        try {
            String teksTersandi = dekodeStringBase64(strBase64);
            String laporanNilai = eksekusiPerhitunganNilaiAkademik(teksTersandi);
            return ResponseEntity.ok(laporanNilai);
        } catch (NoSuchElementException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            return new ResponseEntity<>("Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar.", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/perbedaanL/{strBase64}")
    public ResponseEntity<String> perbedaanL(@PathVariable String strBase64) {
        try {
            String dataMatriksTersembunyi = dekodeStringBase64(strBase64);
            String analisisPatternL = prosesPenyelidikanPatternL(dataMatriksTersembunyi);
            return ResponseEntity.ok(analisisPatternL);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Format data matriks tidak valid atau tidak lengkap.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/palingTer/{strBase64}")
    public ResponseEntity<String> palingTer(@PathVariable String strBase64) {
        try {
            String kumpulanAngkaMisterius = dekodeStringBase64(strBase64);
            String statistikUnik = analisisFrekuensiTersembunyi(kumpulanAngkaMisterius);
            return ResponseEntity.ok(statistikUnik);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        }
    }

    private String dekodeStringBase64(String kodeTersembunyi) {
        try {
            byte[] dataTerpecahkan = Base64.getDecoder().decode(kodeTersembunyi);
            return new String(dataTerpecahkan, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("Input Base64 tidak valid: " + e.getMessage());
        }
    }

    private String konversiKeHurufMutu(double skorAkhir) {
        if (skorAkhir >= AMBANG_BATAS_NILAI_SANGAT_MEMUASKAN) return "A";
        else if (skorAkhir >= AMBANG_BATAS_NILAI_HAMPIR_SANGAT_MEMUASKAN) return "AB";
        else if (skorAkhir >= AMBANG_BATAS_NILAI_MEMUASKAN) return "B";
        else if (skorAkhir >= AMBANG_BATAS_NILAI_CUKUP_MEMUASKAN) return "BC";
        else if (skorAkhir >= AMBANG_BATAS_NILAI_SEDANG) return "C";
        else if (skorAkhir >= AMBANG_BATAS_NILAI_PERLU_PERBAIKAN) return "D";
        else return "E";
    }

    private String prosesDecodingNim(String kodeRahasiaNim) {
        StringBuilder penampilHasil = new StringBuilder();

        if (kodeRahasiaNim.length() != 8) {
            throw new IllegalArgumentException("Format NIM tidak valid. Harap masukkan 8 digit.");
        }

        String identitasProgram = kodeRahasiaNim.substring(0, 3);
        String kodeTahunMasuk = kodeRahasiaNim.substring(3, 5);
        String nomorIdentitasUnik = kodeRahasiaNim.substring(5);
        String namaProgramTerjemahan = KAMUS_PROGRAM_STUDI_RAHASIA.get(identitasProgram);

        if (namaProgramTerjemahan != null) {
            int tahunKedatangan = 2000 + Integer.parseInt(kodeTahunMasuk);
            penampilHasil.append("Inforamsi NIM ").append(kodeRahasiaNim).append(": \n");
            penampilHasil.append(">> Program Studi: ").append(namaProgramTerjemahan).append("\n");
            penampilHasil.append(">> Angkatan: ").append(tahunKedatangan).append("\n");
            penampilHasil.append(">> Urutan: ").append(Integer.parseInt(nomorIdentitasUnik));
        } else {
            throw new IllegalArgumentException("Prefix NIM '" + identitasProgram + "' tidak ditemukan.");
        }
        return penampilHasil.toString();
    }

    private String eksekusiPerhitunganNilaiAkademik(String dataNilaiMentah) {
        StringBuilder pembangunLaporan = new StringBuilder();
        try (Scanner pemindaiData = new Scanner(dataNilaiMentah)) {
            pemindaiData.useLocale(Locale.US);

            int poinPartisipasi = pemindaiData.nextInt();
            int poinPenugasan = pemindaiData.nextInt();
            int poinKuisCepat = pemindaiData.nextInt();
            int poinProyekAkhir = pemindaiData.nextInt();
            int poinUjianTengah = pemindaiData.nextInt();
            int poinUjianAkhir = pemindaiData.nextInt();
            pemindaiData.nextLine();

            int akumulasiPartisipasi = 0, batasPartisipasi = 0;
            int akumulasiPenugasan = 0, batasPenugasan = 0;
            int akumulasiKuis = 0, batasKuis = 0;
            int akumulasiProyek = 0, batasProyek = 0;
            int akumulasiUTS = 0, batasUTS = 0;
            int akumulasiUAS = 0, batasUAS = 0;

            while (pemindaiData.hasNextLine()) {
                String barisData = pemindaiData.nextLine().trim();
                if (barisData.equals("---")) break;

                String[] pecahanData = barisData.split("\\|");
                String kodeKomponen = pecahanData[0];
                int nilaiMaksimum = Integer.parseInt(pecahanData[1]);
                int nilaiDicapai = Integer.parseInt(pecahanData[2]);

                switch (kodeKomponen) {
                    case "PA": batasPartisipasi += nilaiMaksimum; akumulasiPartisipasi += nilaiDicapai; break;
                    case "T": batasPenugasan += nilaiMaksimum; akumulasiPenugasan += nilaiDicapai; break;
                    case "K": batasKuis += nilaiMaksimum; akumulasiKuis += nilaiDicapai; break;
                    case "P": batasProyek += nilaiMaksimum; akumulasiProyek += nilaiDicapai; break;
                    case "UTS": batasUTS += nilaiMaksimum; akumulasiUTS += nilaiDicapai; break;
                    case "UAS": batasUAS += nilaiMaksimum; akumulasiUAS += nilaiDicapai; break;
                    default: break;
                }
            }

            double persentasePartisipasi = (batasPartisipasi == 0) ? 0 : (akumulasiPartisipasi * 100.0 / batasPartisipasi);
            double persentasePenugasan = (batasPenugasan == 0) ? 0 : (akumulasiPenugasan * 100.0 / batasPenugasan);
            double persentaseKuis = (batasKuis == 0) ? 0 : (akumulasiKuis * 100.0 / batasKuis);
            double persentaseProyek = (batasProyek == 0) ? 0 : (akumulasiProyek * 100.0 / batasProyek);
            double persentaseUTS = (batasUTS == 0) ? 0 : (akumulasiUTS * 100.0 / batasUTS);
            double persentaseUAS = (batasUAS == 0) ? 0 : (akumulasiUAS * 100.0 / batasUAS);

            int partisipasiBulat = (int) Math.round(persentasePartisipasi);
            int penugasanBulat = (int) Math.round(persentasePenugasan);
            int kuisBulat = (int) Math.round(persentaseKuis);
            int proyekBulat = (int) Math.round(persentaseProyek);
            int UTSBulat = (int) Math.round(persentaseUTS);
            int UASBulat = (int) Math.round(persentaseUAS);

            double bobotPartisipasi = (partisipasiBulat / 100.0) * poinPartisipasi;
            double bobotPenugasan = (penugasanBulat / 100.0) * poinPenugasan;
            double bobotKuis = (kuisBulat / 100.0) * poinKuisCepat;
            double bobotProyek = (proyekBulat / 100.0) * poinProyekAkhir;
            double bobotUTS = (UTSBulat / 100.0) * poinUjianTengah;
            double bobotUAS = (UASBulat / 100.0) * poinUjianAkhir;

            double totalNilaiAkhir = bobotPartisipasi + bobotPenugasan + bobotKuis + bobotProyek + bobotUTS + bobotUAS;

            pembangunLaporan.append("Perolehan Nilai:\n");
            pembangunLaporan.append(String.format(Locale.US, ">> Partisipatif: %d/100 (%.2f/%d)\n", partisipasiBulat, bobotPartisipasi, poinPartisipasi));
            pembangunLaporan.append(String.format(Locale.US, ">> Tugas: %d/100 (%.2f/%d)\n", penugasanBulat, bobotPenugasan, poinPenugasan));
            pembangunLaporan.append(String.format(Locale.US, ">> Kuis: %d/100 (%.2f/%d)\n", kuisBulat, bobotKuis, poinKuisCepat));
            pembangunLaporan.append(String.format(Locale.US, ">> Proyek: %d/100 (%.2f/%d)\n", proyekBulat, bobotProyek, poinProyekAkhir));
            pembangunLaporan.append(String.format(Locale.US, ">> UTS: %d/100 (%.2f/%d)\n", UTSBulat, bobotUTS, poinUjianTengah));
            pembangunLaporan.append(String.format(Locale.US, ">> UAS: %d/100 (%.2f/%d)\n", UASBulat, bobotUAS, poinUjianAkhir));
            pembangunLaporan.append("\n");
            pembangunLaporan.append(String.format(Locale.US, ">> Nilai Akhir: %.2f\n", totalNilaiAkhir));
            pembangunLaporan.append(String.format(Locale.US, ">> Grade: %s\n", konversiKeHurufMutu(totalNilaiAkhir)));
        }
        return pembangunLaporan.toString().trim();
    }

    private String prosesPenyelidikanPatternL(String dataGridRahasia) {
        StringBuilder penulisAnalisis = new StringBuilder();
        try (Scanner pemindaiGrid = new Scanner(dataGridRahasia)) {
            int dimensiMatriks = pemindaiGrid.nextInt();
            int[][] kumpulanDataGrid = new int[dimensiMatriks][dimensiMatriks];
            for (int baris = 0; baris < dimensiMatriks; baris++) {
                for (int kolom = 0; kolom < dimensiMatriks; kolom++) {
                    kumpulanDataGrid[baris][kolom] = pemindaiGrid.nextInt();
                }
            }

            if (dimensiMatriks == 1) {
                int nilaiPusat = kumpulanDataGrid[0][0];
                penulisAnalisis.append("Nilai L: Tidak Ada\n");
                penulisAnalisis.append("Nilai Kebalikan L: Tidak Ada\n");
                penulisAnalisis.append("Nilai Tengah: ").append(nilaiPusat).append("\n");
                penulisAnalisis.append("Perbedaan: Tidak Ada\n");
                penulisAnalisis.append("Dominan: ").append(nilaiPusat);
                return penulisAnalisis.toString();
            }

            if (dimensiMatriks == 2) {
                int totalSemua = 0;
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        totalSemua += kumpulanDataGrid[i][j];
                    }
                }
                penulisAnalisis.append("Nilai L: Tidak Ada\n");
                penulisAnalisis.append("Nilai Kebalikan L: Tidak Ada\n");
                penulisAnalisis.append("Nilai Tengah: ").append(totalSemua).append("\n");
                penulisAnalisis.append("Perbedaan: Tidak Ada\n");
                penulisAnalisis.append("Dominan: ").append(totalSemua);
                return penulisAnalisis.toString();
            }

            int totalPatternL = kalkulasiTotalPatternL(kumpulanDataGrid, dimensiMatriks);
            int totalPatternLBalik = kalkulasiTotalPatternLBalik(kumpulanDataGrid, dimensiMatriks);
            int nilaiIntiPusat = kalkulasiNilaiIntiPusat(kumpulanDataGrid, dimensiMatriks);
            int selisihAntaraPattern = Math.abs(totalPatternL - totalPatternLBalik);
            int nilaiDominan = (selisihAntaraPattern == 0) ? nilaiIntiPusat : Math.max(totalPatternL, totalPatternLBalik);

            penulisAnalisis.append("Nilai L: ").append(totalPatternL).append(":\n");
            penulisAnalisis.append("Nilai Kebalikan L: ").append(totalPatternLBalik).append("\n");
            penulisAnalisis.append("Nilai Tengah: ").append(nilaiIntiPusat).append("\n");
            penulisAnalisis.append("Perbedaan: ").append(selisihAntaraPattern).append("\n");
            penulisAnalisis.append("Dominan: ").append(nilaiDominan);
        }
        return penulisAnalisis.toString().trim();
    }

    private int kalkulasiTotalPatternL(int[][] gridData, int ukuranDimensi) {
        int akumulasiTotal = 0;
        for (int posisiBaris = 0; posisiBaris < ukuranDimensi; posisiBaris++) {
            akumulasiTotal += gridData[posisiBaris][0];
        }
        for (int posisiKolom = 1; posisiKolom < ukuranDimensi - 1; posisiKolom++) {
            akumulasiTotal += gridData[ukuranDimensi - 1][posisiKolom];
        }
        return akumulasiTotal;
    }

    private int kalkulasiTotalPatternLBalik(int[][] gridData, int ukuranDimensi) {
        int akumulasiTotalBalik = 0;
        for (int posisiBaris = 0; posisiBaris < ukuranDimensi; posisiBaris++) {
            akumulasiTotalBalik += gridData[posisiBaris][ukuranDimensi - 1];
        }
        for (int posisiKolom = 1; posisiKolom < ukuranDimensi - 1; posisiKolom++) {
            akumulasiTotalBalik += gridData[0][posisiKolom];
        }
        return akumulasiTotalBalik;
    }

    private int kalkulasiNilaiIntiPusat(int[][] gridData, int ukuranDimensi) {
        if (ukuranDimensi % 2 == 1) {
            return gridData[ukuranDimensi / 2][ukuranDimensi / 2];
        } else {
            int titikTengah1 = ukuranDimensi / 2 - 1;
            int titikTengah2 = ukuranDimensi / 2;
            return gridData[titikTengah1][titikTengah1] + gridData[titikTengah1][titikTengah2] + gridData[titikTengah2][titikTengah1] + gridData[titikTengah2][titikTengah2];
        }
    }

    private String analisisFrekuensiTersembunyi(String deretAngkaMisterius) {
        StringBuilder generatorStatistik = new StringBuilder();
        try (Scanner pemindaiAngka = new Scanner(deretAngkaMisterius)) {
            List<Integer> kumpulanBilangan = new ArrayList<>();
            while (pemindaiAngka.hasNextInt()) {
                kumpulanBilangan.add(pemindaiAngka.nextInt());
            }

            if (kumpulanBilangan.isEmpty()) {
                generatorStatistik.append("Tidak ada input");
                return generatorStatistik.toString();
            }

            Map<Integer, Integer> petaFrekuensi = new LinkedHashMap<>();
            int nilaiPuncak = Integer.MIN_VALUE;
            int nilaiTerendah = Integer.MAX_VALUE;
            int bilanganTerpopuler = kumpulanBilangan.get(0);
            int frekuensiTerbanyak = 0;

            for (int bilangan : kumpulanBilangan) {
                petaFrekuensi.put(bilangan, petaFrekuensi.getOrDefault(bilangan, 0) + 1);
                int frekuensiSaatIni = petaFrekuensi.get(bilangan);
                if (frekuensiSaatIni > frekuensiTerbanyak) {
                    frekuensiTerbanyak = frekuensiSaatIni;
                    bilanganTerpopuler = bilangan;
                }
                if (bilangan > nilaiPuncak) nilaiPuncak = bilangan;
                if (bilangan < nilaiTerendah) nilaiTerendah = bilangan;
            }

            Set<Integer> himpunanTereleminasi = new HashSet<>();
            int bilanganTersendiri = -1;
            int posisiSekarang = 0;
            while (posisiSekarang < kumpulanBilangan.size()) {
                int bilanganKini = kumpulanBilangan.get(posisiSekarang);
                if (himpunanTereleminasi.contains(bilanganKini)) {
                    posisiSekarang++;
                    continue;
                }
                int posisiBerikutnya = posisiSekarang + 1;
                while (posisiBerikutnya < kumpulanBilangan.size() && kumpulanBilangan.get(posisiBerikutnya) != bilanganKini) {
                    posisiBerikutnya++;
                }
                if (posisiBerikutnya < kumpulanBilangan.size()) {
                    for (int indeks = posisiSekarang + 1; indeks < posisiBerikutnya; indeks++) {
                        himpunanTereleminasi.add(kumpulanBilangan.get(indeks));
                    }
                    himpunanTereleminasi.add(bilanganKini);
                    posisiSekarang = posisiBerikutnya + 1;
                } else {
                    bilanganTersendiri = bilanganKini;
                    break;
                }
            }

            if (bilanganTersendiri == -1) {
                generatorStatistik.append("Tidak ada angka unik");
                return generatorStatistik.toString();
            }

            int nilaiProdukTertinggi = -1;
            int hitungProdukTertinggi = -1;
            long hasilKaliTertinggi = Long.MIN_VALUE;
            for (Map.Entry<Integer, Integer> entri : petaFrekuensi.entrySet()) {
                int nilaiKunci = entri.getKey();
                int jumlahKemunculan = entri.getValue();
                long produkPerkalian = (long) nilaiKunci * jumlahKemunculan;
                if (produkPerkalian > hasilKaliTertinggi || (produkPerkalian == hasilKaliTertinggi && nilaiKunci > nilaiProdukTertinggi)) {
                    hasilKaliTertinggi = produkPerkalian;
                    nilaiProdukTertinggi = nilaiKunci;
                    hitungProdukTertinggi = jumlahKemunculan;
                }
            }

            int nilaiProdukTerendah = nilaiTerendah;
            int hitungProdukTerendah = petaFrekuensi.get(nilaiTerendah);
            long hasilKaliTerendah = (long) nilaiProdukTerendah * hitungProdukTerendah;

            generatorStatistik.append("Tertinggi: ").append(nilaiPuncak).append("\n");
            generatorStatistik.append("Terendah: ").append(nilaiTerendah).append("\n");
            generatorStatistik.append("Terbanyak: ").append(bilanganTerpopuler).append(" (").append(frekuensiTerbanyak).append("x)\n");
            generatorStatistik.append("Tersedikit: ").append(bilanganTersendiri).append(" (").append(petaFrekuensi.get(bilanganTersendiri)).append("x)\n");
            generatorStatistik.append("Jumlah Tertinggi: ").append(nilaiProdukTertinggi).append(" * ").append(hitungProdukTertinggi).append(" = ").append(hasilKaliTertinggi).append("\n");
            generatorStatistik.append("Jumlah Terendah: ").append(nilaiProdukTerendah).append(" * ").append(hitungProdukTerendah).append(" = ").append(hasilKaliTerendah);
        }
        return generatorStatistik.toString().trim();
    }
}
