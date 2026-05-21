import esBisiesto from "./esBisiesto.js";

describe("Es bisiesto", () => {
  it("Todo numero divisible entre 400 son bisiestos", () => {
    const result = esBisiesto(2000)
    expect(result).toEqual(true);
  });

});
