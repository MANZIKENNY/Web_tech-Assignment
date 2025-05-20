import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ConvertNumber")
public class NumberConverterServlet extends HttpServlet {

    // Handle GET requests
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        displayForm(response, "");
    }

    // Handle POST requests (form submissions)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String numberStr = request.getParameter("number");
        String convertType = request.getParameter("convert");
        String clear = request.getParameter("clear");

        if (clear != null) {
            numberStr = ""; // Reset the number when "Clear" is pressed
        }

        // Process the form if the number is entered
        if (numberStr != null && !numberStr.isEmpty() && convertType != null) {
            try {
                int number = Integer.parseInt(numberStr);
                String result = "";

                switch (convertType) {
                    case "binary":
                        result = Integer.toBinaryString(number);
                        break;
                    case "octal":
                        result = Integer.toOctalString(number);
                        break;
                    case "hexadecimal":
                        result = Integer.toHexString(number);
                        break;
                }

                // Return result
                displayForm(response, "Number Entered: " + number + "<br>Base Converted to: " + convertType.substring(0, 1).toUpperCase() + convertType.substring(1) + "<br>Result: " + result);
            } catch (NumberFormatException e) {
                displayForm(response, "<h2>Invalid Number Format</h2>");
            }
        } else {
            displayForm(response, "");
        }
    }

    private void displayForm(HttpServletResponse response, String resultMessage) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f9; }");
        out.println("h1 { color: #333; }");
        out.println("input[type='text'] { padding: 10px; margin: 10px 0; width: 200px; border: 1px solid #ccc; border-radius: 4px; }");
        out.println("button { padding: 10px 20px; margin: 5px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer; }");
        out.println("button:hover { background-color: #45a049; }");
        out.println("div { margin-bottom: 20px; }");
        out.println(".result { margin-top: 20px; padding: 10px; background-color: #e7e7e7; border-radius: 4px; }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<h1>Converter</h1>");

        out.println("<form method='POST'>");
        out.println("<label for='number'>Enter Base 10 Number:</label>");
        out.println("<input type='text' id='number' name='number'>");
        out.println("<div>");
        out.println("<button type='submit' name='convert' value='binary'>Binary</button>");
        out.println("<button type='submit' name='convert' value='octal'>Octal</button>");
        out.println("<button type='submit' name='convert' value='hexadecimal'>Hexadecimal</button>");
        out.println("<button type='submit' name='clear' value='clear'>Clear</button>");
        out.println("</div>");
        out.println("</form>");

        if (!resultMessage.isEmpty()) {
            out.println("<div class='result'>");
            out.println(resultMessage);
            out.println("</div>");
        }

        out.println("</body></html>");
    }
}
