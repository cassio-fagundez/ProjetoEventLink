package com.example.projetoeventlink.Logica;

public class DocumentValidator {

    public static boolean validarCPF(String cpf) {
        try {
            // Eliminar cualquier carácter no numérico
            cpf = cleanDocument(cpf);

            // Verificar si el CPF tiene la longitud correcta
            if (cpf.length() != 11) {
                return false;
            }

            // Verificar CPFs inválidos conocidos
            if (cpf.equals("00000000000") || cpf.equals("11111111111") || cpf.equals("22222222222") ||
                    cpf.equals("33333333333") || cpf.equals("44444444444") || cpf.equals("55555555555") ||
                    cpf.equals("66666666666") || cpf.equals("77777777777") || cpf.equals("88888888888") ||
                    cpf.equals("99999999999")) {
                return false;
            }

            // Validar primer dígito verificador
            int add = 0;
            for (int i = 0; i < 9; i++) {
                add += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int rev = 11 - (add % 11);
            if (rev == 10 || rev == 11) {
                rev = 0;
            }
            if (rev != Character.getNumericValue(cpf.charAt(9))) {
                return false;
            }

            // Validar segundo dígito verificador
            add = 0;
            for (int i = 0; i < 10; i++) {
                add += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            rev = 11 - (add % 11);
            if (rev == 10 || rev == 11) {
                rev = 0;
            }
            if (rev != Character.getNumericValue(cpf.charAt(10))) {
                return false;
            }

            return true;

        } catch (Exception e) {
            // Captura cualquier excepción que pueda ocurrir y devuelve false
            return false;
        }
    }

    public static boolean validarCI(String ci) {
        try {
            // Limpiar la CI eliminando cualquier carácter no numérico
            ci = cleanDocument(ci);

            // Si la longitud es menor a 7, rellenar con ceros al principio
            while (ci.length() < 7) {
                ci = "0" + ci;
            }

            if (ci.length() > 8 ) { return false; }

            // Verificar CI inválidos conocidos
            if (ci.equals("0000000") || ci.equals("1111111") || ci.equals("2222222") ||
                    ci.equals("3333333") || ci.equals("4444444") || ci.equals("5555555") ||
                    ci.equals("6666666") || ci.equals("7777777") || ci.equals("8888888") ||
                    ci.equals("9999999")) {
                return false;
            }

            // Obtener el dígito verificador de la CI
            int dig = Character.getNumericValue(ci.charAt(ci.length() - 1));
            // Eliminar el dígito verificador para poder validar el resto de la CI
            ci = ci.substring(0, ci.length() - 1);

            // Validar el dígito verificador calculado
            return dig == validationDigit(ci);

        } catch (Exception e) {
            // Captura cualquier excepción que pueda ocurrir y devuelve false
            return false;
        }
    }

    private static int validationDigit(String ci) {
        try {
            // La secuencia de multiplicación para calcular el dígito verificador
            String seq = "2987634";
            int sum = 0;

            // Realizar la multiplicación de los primeros 7 dígitos de la CI con la secuencia
            for (int i = 0; i < 7; i++) {
                sum += (Character.getNumericValue(seq.charAt(i)) * Character.getNumericValue(ci.charAt(i))) % 10;
            }

            // Calcular el dígito verificador
            if (sum % 10 == 0) {
                return 0;
            } else {
                return 10 - sum % 10;
            }

        } catch (Exception e) {
            // Captura cualquier excepción que pueda ocurrir y devuelve 0
            return 0;
        }
    }

    public static String cleanDocument(String ci) {
        // Eliminar cualquier carácter no numérico
        return ci.replaceAll("\\D", "");
    }

}
