import matplotlib.pyplot as plt

# Input points
x1 = int(input("Enter x1: "))
y1 = int(input("Enter y1: "))
x2 = int(input("Enter x2: "))
y2 = int(input("Enter y2: "))

# DDA calculations
dx = x2 - x1
dy = y2 - y1
steps = max(abs(dx), abs(dy))

x_inc = dx / steps
y_inc = dy / steps

x = x1
y = y1

x_pixels = []
y_pixels = []

# Generate DDA pixels
for i in range(steps + 1):
    px = round(x)
    py = round(y)

    x_pixels.append(px)
    y_pixels.append(py)

    print("Pixel:", (px, py))

    x += x_inc
    y += y_inc   # ✅ fixed typo

# Plot DDA pixels (discrete points)
plt.scatter(x_pixels, y_pixels, marker='*', s=100, color="red", label="DDA Pixels")

# Plot true straight line between endpoints
plt.plot([x1, x2], [y1, y2], color="blue", label="Ideal Line")

plt.title("DDA Line Drawing")
plt.xlabel("X - Axis")
plt.ylabel("Y - Axis")
plt.grid(True)
plt.legend()
plt.axis("equal")  # keep aspect ratio square
plt.show()