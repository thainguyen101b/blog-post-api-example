## Tổng quan về Clean Architecture và DDD

Clean Architecture, do Robert C. Martin đề xuất, nhấn mạnh việc tách biệt các tầng dựa trên giá trị nghiệp vụ, với các
tầng chính bao gồm:

- **Entities** (Tầng domain): Chứa các đối tượng nghiệp vụ (business objects), chẳng hạn như User, Order.
- **Use Cases** (Tầng ứng dụng): Chứa logic ứng dụng, bao gồm các use case như tạo, cập nhật, hoặc truy vấn dữ liệu.
- **Interface Adapters**: Xử lý giao tiếp với bên ngoài, như controller, DTOs, và mapping dữ liệu.
- **Frameworks and Drivers** (Tầng hạ tầng): Bao gồm các công cụ như Spring Boot, cơ sở dữ liệu, và các thư viện bên
  ngoài.

DDD, mặt khác, tập trung vào việc mô hình hóa domain sao cho phản ánh đúng quy trình và quy tắc nghiệp vụ thực tế, với
các khái niệm như entities, value objects, aggregates, và repositories. Repositories thường được định nghĩa ở tầng
domain để cung cấp giao diện truy cập dữ liệu, trong khi tầng hạ tầng cung cấp triển khai cụ thể.