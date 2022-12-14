package dk.alexandra.fresco.lib.common.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Matrix<T> {

  private final int width;
  private final int height;
  private final ArrayList<ArrayList<T>> matrix;

  /**
   * Creates an matrix from a row building function.
   * 
   * @param height height of the matrix
   * @param width width of the matrix
   * @param rowBuilder the function for building rows
   */
  public Matrix(int height, int width, IntFunction<ArrayList<T>> rowBuilder) {
    this.width = width;
    this.matrix = new ArrayList<>(height);
    this.height = height;
    for (int i = 0; i < height; i++) {
      this.matrix.add(rowBuilder.apply(i));
    }
  }

  /**
   * Create a new function with the given entry building function.
   *
   * @param height height of the matrix
   * @param width width of the matrix
   * @param builder function for building entries given the indices of row and column resp.
   */
  public Matrix(int height, int width, BiFunction<Integer, Integer, T> builder) {
    this(height, width, i -> IntStream.range(0, width).mapToObj(j -> builder.apply(i, j)).collect(
        Collectors.toCollection(ArrayList::new)));
  }

  /**
   * Clones matrix.
   * 
   * @param other The matrix to be cloned
   */
  public Matrix(Matrix<T> other) {
    this.width = other.getWidth();
    this.height = other.getHeight();
    this.matrix = other.getRows().stream().map(ArrayList::new)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  /**
   * Creates a matrix directly from an ArrayList of ArrayLists.
   * 
   * @param height height of the matrix
   * @param width width of the matrix
   * @param matrix the array data
   */
  public Matrix(int height, int width, ArrayList<ArrayList<T>> matrix) {
    this.width = width;
    this.height = height;
    this.matrix = matrix;
  }

  /**
   * Gets all rows of the matrix.
   * 
   * <p>Convenience method for iterating over the rows of the matrix.</p>
   * 
   * @return A concatenated list of rows
   */
  public ArrayList<ArrayList<T>> getRows() {
    return this.matrix;
  }

  public ArrayList<T> getRow(int i) {
    return matrix.get(i);
  }

  public void setRow(int i, ArrayList<T> row) {
    matrix.set(i, row);
  }

  /**
   * Gets the width of the matrix.
   * 
   * @return the width of the matrix
   */
  public int getWidth() {
    return width;
  }


  /**
   * Gets the height of the matrix.
   * 
   * @return the height of the matrix
   */
  public int getHeight() {
    return height;
  }

  public ArrayList<T> getColumn(int i) {
    return this.matrix.stream().map(row -> row.get(i)).collect(Collectors.toCollection(ArrayList::new));
  }

  @Override
  public String toString() {
    return "Matrix{" + "width=" + width + ", height=" + height + ", matrix=" + matrix + '}';
  }

}
